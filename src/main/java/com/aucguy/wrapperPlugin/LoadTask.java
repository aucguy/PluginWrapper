package com.aucguy.wrapperPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.tasks.TaskAction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.aucguy.wrapperPlugin.util.ConfigException;
import com.aucguy.wrapperPlugin.util.DomUtil;
import com.aucguy.wrapperPlugin.util.Invoker;
import com.aucguy.wrapperPlugin.util.MavenCoord;

/**
 * task for loading the wrapped plugin
 */
public class LoadTask extends DefaultTask {
	private static final String INVOKER_CL = "com.aucguy.wrapperPlugin.util.Invoker";
	private static final String PLUGIN_MANIFEST = "META-INF/gradle-plugins/%s.properties";
	private static final String IMPL_PROP = "implementation-class";
	private static final String GROUP_PREFIX = "wrapped.";
	private static final String CUSTOM_CONFIG = "pompath";
	private static final String ZIP_ENTRY = "wrappedMarker";
	private static final String ZIP_CONTENT = "File made by WrapperPlugin";
	private static final String GROUP_ID = "groupId";
	public static final String TASK_NAME = "loadWrappedPlugin";
	
	@TaskAction
    public void doTask() throws MalformedURLException, IOException, SAXException, ParserConfigurationException, InstantiationException, 
    		IllegalAccessException, ClassNotFoundException, TransformerException {
		//get and check extension properties
		WrapperExtension ext = (WrapperExtension) getProject().getExtensions().getByName(WrapperPlugin.EXTENSION);
		String plugin = ext.getPlugin();
		String path = ext.getClasspath();
		String pomId = ext.getPom();
		String extraDep = ext.getExtraDep(); //can be null
		ConfigException.raiseIfNull(plugin, "plugin");
		ConfigException.raiseIfNull(path, "classpath");
		ConfigException.raiseIfNull(pomId, "pom");
		
		//create coordinates
		MavenArtifactRepository repo = getProject().getRepositories().mavenLocal(); 
		MavenCoord inputCoord = new MavenCoord(pomId);
		MavenCoord outputCoord = new MavenCoord(pomId);
		outputCoord.groupId = GROUP_PREFIX + outputCoord.groupId;
		
		//do everything else
		copyPOM(repo, inputCoord, outputCoord);
		createZip(repo, outputCoord);
		Set<File> libs = getClasspath(outputCoord, extraDep, path);
		runPlugin(libs, plugin);
	}
	
	private void copyPOM(MavenArtifactRepository repo, MavenCoord inputCoord, MavenCoord outputCoord) throws SAXException, IOException, TransformerException {
		Document pom = DomUtil.parseDocument(inputCoord.getPomPath(repo));
		Element groupId = (Element) pom.getDocumentElement().getElementsByTagName(GROUP_ID).item(0);
		groupId.setTextContent(GROUP_PREFIX + groupId.getTextContent());
		DomUtil.saveDocument(pom, outputCoord.getPomPath(repo));
	}
	
	private void createZip(MavenArtifactRepository repo, MavenCoord outputCoord) throws IOException {
		ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(outputCoord.getJarPath(repo)));
		zip.putNextEntry(new ZipEntry(ZIP_ENTRY));
		zip.write(ZIP_CONTENT.getBytes());
		zip.closeEntry();
		zip.close();
	}
	
	private Set<File> getClasspath(MavenCoord outputCoord, String extraDep, String path) {
		//add to configuration
		DependencyHandler classpath = getProject().getBuildscript().getDependencies();
		Configuration pompath = getProject().getBuildscript().getConfigurations().create(CUSTOM_CONFIG);
		pompath.setTransitive(true);
		classpath.add(CUSTOM_CONFIG, outputCoord.toString());
		
		//extra dependencies
		String parts[];
		if(extraDep != null) {
			parts = extraDep.split(";");
			for(String part : parts) {
				classpath.add(CUSTOM_CONFIG, part);
			}
		}
		
		//initialize files
		Set<File> libs = pompath.resolve();
		
		//convert to URLs
		parts = path.split(";");
		for(int i=0; i<parts.length; i++) {
			libs.add(new File(parts[i]));
		}
		
		return libs;
	}
	
	private URL[] convertToURLs(Set<File> files) throws MalformedURLException {
		URL[] urls = new URL[files.size()];
		int i = 0;
		for(File file : files) {
			urls[i++] = file.toURI().toURL();
		}
		return urls;
	}
	
	private void runPlugin(Set<File> libs, String plugin) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		//get plugin properties and load stuff
		@SuppressWarnings("resource")
		ClassLoader classloader = new URLClassLoader(convertToURLs(libs), this.getClass().getClassLoader());
		Properties properties = new Properties();
		properties.load(classloader.getResourceAsStream(String.format(PLUGIN_MANIFEST, plugin)));
		
		//run plugin
		String impl = properties.getProperty(IMPL_PROP);
		@SuppressWarnings("unchecked")
		Plugin<Project> main = (Plugin<Project>) classloader.loadClass(impl).newInstance();
		WrapperPlugin.instance.invoker = (Invoker) classloader.loadClass(INVOKER_CL).newInstance();
		main.apply(WrapperPlugin.instance.wrappedProject);
	}
}
