package com.aucguy.wrapperPlugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import com.aucguy.wrapperPlugin.util.Invoker;

/**
 * main plugin class
 */
public class WrapperPlugin implements Plugin<Project> {
	public static WrapperPlugin instance; //the instance
	public static final String EXTENSION = "wrapper";
	
	Project wrappedProject; //project with overwritten methods
	Invoker invoker; //invoking thing loaded through the wrapped plugin
	
	@Override
	public void apply(Project project) {
		instance = this;
		try {
			wrappedProject = new WrappedProject(project); //create wrapped project and extension
			wrappedProject.getExtensions().create(EXTENSION, WrapperExtension.class);
			wrappedProject.getTasks().create(LoadTask.TASK_NAME, LoadTask.class); //add tasks
			wrappedProject.getTasks().create(PrintClasspathTask.TASK_NAME, PrintClasspathTask.class);
		} catch (Exception error) {
			throw new RuntimeException(error);
		}
	}
}
