package com.aucguy.wrapperPlugin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.tasks.TaskAction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.aucguy.wrapperPlugin.util.ConfigException;
import com.aucguy.wrapperPlugin.util.DomUtil;
import com.aucguy.wrapperPlugin.util.Invoker;

/**
 * task for loading the wrapped plugin
 */
public class LoadTask extends DefaultTask {
	private static final String INVOKER_CL = "com.aucguy.wrapperPlugin.util.Invoker";
	private static final String PLUGIN_MANIFEST = "META-INF/gradle-plugins/%s.properties";
	private static final String IMPL_PROP = "implementation-class";
	private static final String ARTIFACT_FORMAT = "{url}/{group}/{artifact}/{version}/{artifact}-{version}.pom";
	public static final String TASK_NAME = "loadWrappedPlugin";
	
	@TaskAction
    public void doTask() throws MalformedURLException, IOException, SAXException, ParserConfigurationException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		//get and check extension properties
		WrapperExtension ext = (WrapperExtension) getProject().getExtensions().getByName(WrapperPlugin.EXTENSION);
		String plugin = ext.getPlugin();
		String path = ext.getClasspath();
		String pomId = ext.getPom();
		String extraDep = ext.getExtraDep(); //can be null
		ConfigException.raiseIfNull(plugin, "plugin");
		ConfigException.raiseIfNull(path, "classpath");
		ConfigException.raiseIfNull(pomId, "pom");
		
		DependencyHandler classpath = getProject().getBuildscript().getDependencies();
		Configuration pompath = getProject().getBuildscript().getConfigurations().create("pompath");
		pompath.setTransitive(false);
		
		//get POM contents
		File pomFile = getPomFile(getProject().getRepositories().mavenLocal(), pomId);
		Document pom = DomUtil.parseDocument(pomFile);
		Node dependencies = pom.getDocumentElement().getElementsByTagName("dependencies").item(0);
		for(Element dependency : DomUtil.iter(dependencies)) {
			if(dependency.getNodeName().equals("dependency")) {
				String group = dependency.getElementsByTagName("groupId").item(0).getTextContent();
				String artifact = dependency.getElementsByTagName("artifactId").item(0).getTextContent();
				String version = dependency.getElementsByTagName("version").item(0).getTextContent();
				String notation = group + ":" + artifact + ":" + version;
				System.out.println("notation = " + notation);
				classpath.add("pompath", notation);
			}
		}
		
		//extra dependencies
		String parts[];
		if(extraDep != null) {
			parts = extraDep.split(";");
			for(String part : parts) {
				classpath.add("pompath", part);
			}
		}
		
		//initialize files
		Set<File> libs = pompath.resolve();
		
		parts = path.split(";");
		for(int i=0; i<parts.length; i++) {
			libs.add(new File(parts[i]));
		}
		
		//get plugin properties and load stuff
		URL[] urls = new URL[libs.size()];
		int i = 0;
		for(File lib : libs) {
			urls[i++] = lib.toURI().toURL();
		}
		
		@SuppressWarnings("resource")
		ClassLoader classloader = new URLClassLoader(urls, this.getClass().getClassLoader());
		Properties properties = new Properties();
		properties.load(classloader.getResourceAsStream(String.format(PLUGIN_MANIFEST, plugin)));
		
		//run plugin
		String impl = properties.getProperty(IMPL_PROP);
		@SuppressWarnings("unchecked")
		Plugin<Project> main = (Plugin<Project>) classloader.loadClass(impl).newInstance();
		WrapperPlugin.instance.invoker = (Invoker) classloader.loadClass(INVOKER_CL).newInstance();
		main.apply(WrapperPlugin.instance.wrappedProject);
	}
	
	private File getPomFile(MavenArtifactRepository repository, String plugin) {
		String[] parts = plugin.split(":");
		String path = ARTIFACT_FORMAT
				.replace("{url}", repository.getUrl().getPath())
				.replace("{group}", parts[0].replace('.', '/'))
				.replace("{artifact}", parts[1])
				.replace("{version}", parts[2]);
		return new File(path);
	}
}
