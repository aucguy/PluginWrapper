package com.aucguy.wrapperPlugin.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * made before I discovered eclipse's verision. Not used for anything
 */
public class DelegateInterfaceGen {
	private static final String METHOD_START = "\t@Override\n\t%s {\n\t\t%sthis.delegate.%s(";
	private static final String METHOD_END = ");\n\t}\n\n";
	private static final String METHOD_CALL = "%s";
	private static final String METHOD_ARG_MIDDLE = ", ";
	private static final String HEADER_IMPORT = "import %s;\n";
	private static final String HEADER_CLASS = "public class %s implements %s {\n\tpublic %2$s delegate;\n\n\tpublic %1$s(%2$s delegate) {\n\t\tthis.delegate = delegate;\n\t}\n\n";
	private static final String HEADER_PACKAGE = "/*generated class; don't modify manually*/\npackage %s;\n\n";
	private static final String HEADER_END = "}";
	private static final Set<String> PRIMITIVES = new HashSet<String>(Arrays.asList("void;boolean;char;byte;short;int;long;double;float;public;throws".split(";")));
	private static final String KEYWORDS = "void|boolean|char|byte|short|int|long|double|float|public|throws|abstract|super";
	private static final Pattern PATTERN_ABSTRACT = Pattern.compile(" abstract ");
	private static final Pattern PATTERN_CLASS = Pattern.compile("(?<!\\w|[.])(?!" + KEYWORDS + ")(\\w+[.])*(?<class>\\w+)(?!\\w|[.])");
	private static final Pattern PATTERN_TYPEARGUMENT = Pattern.compile("public <(?<typeArgs>(\\w+,)*\\w+)>");
	
	protected StringBuilder code = new StringBuilder();
	protected StringBuilder header = new StringBuilder();
	protected Set<String> imports = new HashSet<>();
	protected String className;
	protected String pack;
	protected Class<?> inter;
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		new DelegateInterfaceGen().run(args);
	}
	
	public void run(String[] args) throws ClassNotFoundException, IOException {
		if(args.length != 3) {
			throw(new RuntimeException("wrong number of args"));
		}
		inter = Class.forName(args[0]);
		className = args[1];
		pack = getPackage(className);
		OutputStream output = new FileOutputStream(new File(args[2]));
		output.write(generateClass().getBytes());
		output.close();
	}
	
	public String generateClass() {
		imports.add(inter.getName());
		formatOut(header, HEADER_PACKAGE, pack);
		formatOut(code, HEADER_CLASS, getClassName(className), inter.getSimpleName());
		
		generateMethods(inter);
		code.append(HEADER_END);
		
		for(String i : imports.stream().sorted().collect(Collectors.toList())) {
			String k = i.replace("[", "");
			if(k.startsWith("L")) k = k.substring(1, k.length() - 1);
			String place = getPackage(k);
			if(place.equals(pack) || place.equals("java.lang") || PRIMITIVES.contains(i)) continue;
			formatOut(header, HEADER_IMPORT, i);
		}
		
		return header.toString() + "\n" + code.toString();
	}
	
	public void generateMethods(Class<?> clazz) {
		for(Method method : clazz.getDeclaredMethods()) {
			if(Modifier.isAbstract(method.getModifiers())) {
				String declaration =  processGeneric(method.toGenericString(), method);
				String give = declaration.contains(" void ") ? "" : "return ";
				formatOut(code, METHOD_START, declaration, give, method.getName());
				generateCall(code, method, METHOD_CALL);
				code.append(METHOD_END);
			}
		}
		for(Class<?> i : clazz.getInterfaces()) {
			generateMethods(i);
		}
	}
	
	public void generateCall(StringBuilder code, Method method, String argFormat) {
		int i = 0;
		for(Parameter param : method.getParameters()) {
			formatOut(code, argFormat, param.getName());
			if(i++ != method.getParameterCount() - 1) code.append(METHOD_ARG_MIDDLE);
			imports.add(param.getType().getName());
		}
	}
	
	public String processGeneric(String name, Method method) {
		name = PATTERN_ABSTRACT.matcher(name).replaceAll(" ");
		Matcher matcher = PATTERN_TYPEARGUMENT.matcher(name);
		Set<String> typeArgs = new HashSet<String>();
		if(matcher.lookingAt()) {
			typeArgs.addAll(Arrays.asList(matcher.group("typeArgs").replace(" ", "").split(",")));
		}
		
		matcher = PATTERN_CLASS.matcher(name);
		StringBuffer sb = new StringBuffer();
		while(matcher.find()) {
			matcher.appendReplacement(sb, "${class}");
			if((matcher.end() == name.length() || name.charAt(matcher.end()) != '(')
					&& !typeArgs.contains(matcher.group()) && !getPackage(matcher.group()).equals(pack)) {
				imports.add(matcher.group());
			}
		}
		matcher.appendTail(sb);
		name = sb.toString();
		
		sb = new StringBuffer();
		int bracketLevel = 0, k = 0;
		for(int i=0; i<name.length(); i++) {
			char c = name.charAt(i);
			char extra = '~';
			switch(c) {
			case '<': bracketLevel++; break;
			case '>': bracketLevel--; break;
			case ',': extra = ' ';
			case ')': 
				if(bracketLevel == 0 && k < method.getParameterCount()) {
					sb.append(" ");
					sb.append(method.getParameters()[k++].getName());
				}
			}
			sb.append(c);
			if(extra != '~') sb.append(extra);
		}
		return sb.toString();
	}

	public static String getPackage(String clazz) {
		String[] parts = clazz.split("[.]");
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<parts.length-1; i++) { //everything but the last part
			builder.append(parts[i]);
			if(i != parts.length-2) builder.append(".");
		}
		return builder.toString();
	}
	
	public static String getClassName(String clazz) {
		String[] parts = clazz.split("[.]");
		return parts[parts.length - 1];
	}
	
	public static void formatOut(StringBuilder builder, String format, Object ... args) {
		builder.append(String.format(format, args));
	}
}
