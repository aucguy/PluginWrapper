package com.aucguy.wrapperPlugin;

import java.net.URL;
import java.net.URLClassLoader;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

/**
 * prints the classpath
 */
public class PrintClasspathTask extends DefaultTask {
	public static final String TASK_NAME = "printClasspath";
	
	@TaskAction
	public void doTask() {
		ClassLoader classloader = getClass().getClassLoader();
		if(classloader instanceof URLClassLoader) { //should be the case
			@SuppressWarnings("resource")
			URLClassLoader urlloader = (URLClassLoader) classloader;
			StringBuilder builder = new StringBuilder();
			builder.append("classpath: ");
			for(URL url : urlloader.getURLs()) {
				builder.append(url.toString().replace("file:/", ""));
				builder.append(";");
			}
			System.out.println(builder.toString());
		} else { //just in case if its not a URLClassLoader
			System.out.println("classpath: " + classloader.getClass());
		}
		
	}
}
