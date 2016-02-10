package com.github.aucguy.wrapperPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

import com.github.aucguy.wrapperPlugin.util.ConfigException;
import com.github.aucguy.wrapperPlugin.util.DomUtil;
import com.github.aucguy.wrapperPlugin.util.Invoker;
import com.github.aucguy.wrapperPlugin.util.MavenCoord;

/**
 * This is the task for loading the wrapped plugin. It loads any of its dependencies,
 * loads the classes into a classpath and then executes the plugin. 
 * This task is under the name of {@value #TASK_NAME} and is used via {@value #execute()}.
 * 
 * This task gets all the dependencies in the POM of the already built plugin,
 * the binaries of the plugin and any additional dependencies. It then puts them them into
 * a {@link #URLCLassLoader} and then asks it to load the plugin manifest and then load the
 * plugin class specified in the manifest. An instance of the class is then called.
 * 
 * In order to do this it first creates a dummy maven artifact in the local maven repository.
 * This dummy artifact has the same artifact id and version as the wrapped plugin, but the 
 * group id is prefixed by {@value #GROUP_PREFIX}. The dummy artifact also has a jar with
 * a single file named {@value #ZIP_ENTRY} with the contents of {@value #ZIP_CONTENT}. Since,
 * the dummy artifact's POM is a copy of the wrapped plugin, it has all the dependencies of the
 * orginal. The task then creates a new configuration and adds the dummy artifact to it. Thus,
 * this configuration has all the dependencies of the wrapped plugin.
 * 
 * The task then adds any extra dependencies to the configuration. The configuration is then
 * resolved which results in gradle checking for dependency updates and downloading them as necessary.
 * The resolution then returns a set of files which are the dependency archives, aka the classpath.
 * The plugin binaries are then added to this.
 * 
 * Finally, these are pluged into a {@link URLClassLoader} which then loads the manifest. The manifest
 * then is used to find the plugin class which is subsequently loaded.
 */
public class LoadTask extends DefaultTask {
	//name of the invoker class
	private static final String INVOKER_CL = "com.github.aucguy.wrapperPlugin.util.Invoker";
	//place of the plugin manifest
	private static final String PLUGIN_MANIFEST = "META-INF/gradle-plugins/%s.properties";
	//property name of the plugin class in the manifest
	private static final String IMPL_PROP = "implementation-class";
	//what to prefix the group with for the wrapped dependency
	private static final String GROUP_PREFIX = "wrapped.";
	//name of the internally used configuration
	private static final String CUSTOM_CONFIG = "pompath";
	//name of the single file in the dummy zip
	private static final String ZIP_ENTRY = "wrappedMarker";
	//contents of the single file in the dummy zip
	private static final String ZIP_CONTENT = "File made by WrapperPlugin";
	//tagname of the groupId element in the POM
	private static final String GROUP_ID = "groupId";
	//name of the task
	public static final String TASK_NAME = "loadWrappedPlugin";
	
	/**
	 * This does the task.
	 * @throws MalformedURLException if a file cannot be converted to a URL
	 * @throws IOException if some file couldn't be opened
	 * @throws SAXException if the POM file of the original plugin is not valid xml
	 * @throws InstantiationException if the {@link #Invoker} or the plugin class constructor threw an exception
	 * @throws IllegalAccessException if the plugin class or constructor isn't public
	 * @throws ClassNotFoundException if the class specified in the plugin manifest does not exist
	 * @throws TransformerException if there was a problem saving the modified POM
	 * @throws InterruptedException 
	 */
	@TaskAction
    public void doTask() throws MalformedURLException, IOException, SAXException, InstantiationException, 
    		IllegalAccessException, ClassNotFoundException, TransformerException, InterruptedException {
		//get and check validity of extension properties
		WrapperExtension ext = (WrapperExtension) getProject().getExtensions().getByName(WrapperExtension.EXTENSION);
		String plugin = ext.getPlugin();
		String path = ext.getClasspath();
		String pomId = ext.getPom();
		String extraDep = ext.getExtraDep(); //can be null
		boolean pause = ext.getPause();
		ConfigException.raiseIfNull(plugin, "plugin");
		ConfigException.raiseIfNull(path, "classpath");
		ConfigException.raiseIfNull(pomId, "pom");
		
		//create coordinates
		MavenArtifactRepository repo = WrapperPlugin.instance.repository; 
		MavenCoord inputCoord = new MavenCoord(pomId);
		MavenCoord outputCoord = new MavenCoord(pomId);
		outputCoord.groupId = GROUP_PREFIX + outputCoord.groupId;
		
		//do everything else
		copyPOM(repo, inputCoord, outputCoord);
		createZip(repo, outputCoord);
		Set<File> libs = getClasspath(outputCoord, extraDep, path);
		if(pause) {
			System.out.println("press <enter> to continue");
			Scanner scanner = new Scanner(System.in);
			scanner.nextLine();
			scanner.close();
		}
		runPlugin(libs, plugin);
	}
	
	/**
	 * This copies the POM of one module to another module
	 * @param repo the maven repository to copy the POM from and to
	 * @param inputCoord the maven coordinate of the POM to copy from
	 * @param outputCoord the maven coordinate of the POM to copy to
	 * @throws SAXException if the file is not valid xml
	 * @throws IOException if the input POM cannot be read or the output POM cannot be written
	 * @throws TransformerException if something goes wrong when saving the modified DOM 
	 */
	private void copyPOM(MavenArtifactRepository repo, MavenCoord inputCoord, MavenCoord outputCoord) throws SAXException, IOException, TransformerException {
		Document pom = DomUtil.parseDocument(inputCoord.getPomPath(repo));
		Element groupId = (Element) pom.getDocumentElement().getElementsByTagName(GROUP_ID).item(0);
		groupId.setTextContent(GROUP_PREFIX + groupId.getTextContent());
		DomUtil.saveDocument(pom, outputCoord.getPomPath(repo));
	}
	
	/**
	 * This creates an dummy zip file for the maven coordinate.
	 * It only contains a file named {@value #ZIP_ENTRY} with the contents of {@value #ZIP_CONTENT}
	 * @param repo the repository to create the zip in
	 * @param outputCoord the maven coordinate to create the zip for
	 * @throws IOException if the file cannot be written
	 */
	private void createZip(MavenArtifactRepository repo, MavenCoord outputCoord) throws IOException {
		ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(outputCoord.getJarPath(repo)));
		zip.putNextEntry(new ZipEntry(ZIP_ENTRY));
		zip.write(ZIP_CONTENT.getBytes());
		zip.closeEntry();
		zip.close();
	}
	
	/**
	 * This configures the classpath that the plugin runs under.
	 * It includes any dependencies specified in the POM, any extra dependencies specified in 
	 * {@link #WrapperExtension.extraDeps} and the binaries of the actual plugin specified in
	 * {@link #WrapperExtension.classpath}
	 * @param outputCoord the coordinate of the wrapped plugin
	 * @param extraDep any extra maven dependencies not specified in the POM. 
	 * 		Each maven coordinate is separated by a ";"
	 * @param path the binaries of the plugin
	 * @return
	 */
	private Set<File> getClasspath(MavenCoord outputCoord, String extraDep, String path) {
		//add POM dependencies
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
		
		//initialize maven dependencies
		Set<File> libs = pompath.resolve();
		
		//add plugin binaries
		parts = path.split(";");
		for(int i=0; i<parts.length; i++) {
			libs.add(new File(parts[i]));
		}
		
		return libs;
	}
	
	/**
	 * This takes a set of files and converts them to an array of URLs
	 * @param files the set of files to convert
	 * @return the converted array of files
	 * @throws MalformedURLException something screws up with creating the URL
	 */
	private URL[] convertToURLs(Set<File> files) throws MalformedURLException {
		URL[] urls = new URL[files.size()];
		int i = 0;
		for(File file : files) {
			urls[i++] = file.toURI().toURL();
		}
		return urls;
	}
	
	/**
	 * This runs a plugin under the specified classpath
	 * @param libs the set of jars and folders that are the classpath
	 * @param plugin the id of the plugin to load and run
	 * @throws IOException if the plugin manifest could not be opened or 
	 * 		there was a problem converting the files to URLS
	 * @throws InstantiationException there was an error while creating the invoker or plugin instance
	 * @throws IllegalAccessException the plugin is not private
	 * @throws ClassNotFoundException the class specified in the plugin manifest does not exist
	 */
	private void runPlugin(Set<File> libs, String plugin) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		//get plugin properties and load stuff
		@SuppressWarnings("resource") //not closed because it might be needed later during {@link #Project.afterEvalute}
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
