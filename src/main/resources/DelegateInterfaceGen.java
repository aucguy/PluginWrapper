

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
import java.util.Stack;
import java.util.stream.Collectors;

public class DelegateInterfaceGen {
	private static final String METHOD_START = "\t@Override\n\t%s {\n\t\tthis.delegate.%s(";
	private static final String METHOD_END = ");\n\t}\n\n";
	private static final String METHOD_CALL = "%s";
	private static final String METHOD_ARG_MIDDLE = ", ";
	private static final String HEADER_IMPORT = "import %s;\n";
	private static final String HEADER_CLASS = "public class %s implements %s {\n\tpublic %2$s delegate;\n\n\tpublic %1$s(%2$s delegate) {\n\t\tthis.delegate = delegate;\n\t}\n\n";
	private static final String HEADER_PACKAGE = "/*generated class; don't modify manually*/\npackage %s;\n\n";
	private static final String HEADER_END = "}";
	private static final Set<String> PRIMITIVES = new HashSet<String>(Arrays.asList("void;boolean;char;byte;short;int;long;double;float;public;throws".split(";")));
	private static final String IDENTIFIER_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_.";
	
	protected StringBuilder code = new StringBuilder();
	protected StringBuilder header = new StringBuilder();
	protected Set<String> imports = new HashSet<>();
	protected String className;
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
		OutputStream output = new FileOutputStream(new File(args[2]));
		output.write(generateClass().getBytes());
		output.close();
	}
	
	public String generateClass() {
		String pack = getPackage(className);
		imports.add(inter.getName());
		formatOut(header, HEADER_PACKAGE, pack);
		formatOut(code, HEADER_CLASS, getClassName(className), inter.getSimpleName());
		
		for(Method method : inter.getDeclaredMethods()) {
			if(Modifier.isAbstract(method.getModifiers())) {
				formatOut(code, METHOD_START, processGeneric(method.toGenericString(), method), method.getName());
				generateCall(code, method, METHOD_CALL);
				code.append(METHOD_END);
			}
		}
		
		for(String i : imports.stream().sorted().collect(Collectors.toList())) {
			String k = i.replace("[", "");
			if(k.startsWith("L")) k = k.substring(1, k.length() - 1);
			String place = getPackage(k);
			if(place.equals(pack) || place.equals("java.lang") || PRIMITIVES.contains(i)) continue;
			formatOut(header, HEADER_IMPORT, i);
		}
		code.append(HEADER_END);
		
		return header.toString() + "\n" + code.toString();
	}
	
	public void generateCall(StringBuilder code, Method method, String argFormat) {
		int i = 0;
		for(Parameter param : method.getParameters()) {
			formatOut(code, argFormat, param.getName());
			if(i++ != method.getParameterCount() - 1) code.append(METHOD_ARG_MIDDLE);
			imports.add(param.getType().getName());
		}
	}
	
	public String processGeneric(String typeName, Method method) {
		int start = 0, arg = 0;
		boolean lastIdentifier = false;
		StringBuilder builder = new StringBuilder();
		Stack<Character> enclosings = new Stack<Character>();
		for(int i=0; i<=typeName.length(); i++) {
			char c = i == typeName.length() ? '~' : typeName.charAt(i);
			if(IDENTIFIER_CHARS.indexOf(c) == -1) {
				if(lastIdentifier) {
					String clazz = typeName.substring(start, i);
					start = i + 1;
					switch(clazz) {
					case "super":
					case "extends":
						builder.append(clazz); break;
					case "abstract":
						break;
					default:
						builder.append(getClassName(clazz));
						if(c != '(') imports.add(clazz);
					}
				}
				switch(c){
				case '~':
					continue;
				case '(':
					enclosings.push('(');
					break;
				case '<':
					enclosings.push('<');
					break;
				case ')':
					if(method.getParameterCount() != 0) {
						builder.append(" ");
						builder.append(method.getParameters()[arg++].getName());
					}
				case '>':
					enclosings.pop();
					break;
				case ',':
					if(enclosings.peek().equals('(')) {
						builder.append(" ");
						builder.append(method.getParameters()[arg++].getName());
					}
				}
				builder.append(c);
			}
			lastIdentifier = IDENTIFIER_CHARS.indexOf(c) != -1;
		}
		return builder.toString();
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
