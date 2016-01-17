package com.aucguy.wrapperPlugin;

import org.gradle.api.Action;
import org.gradle.api.Project;

import com.aucguy.wrapperPlugin.util.ProjectDelegate;

/**
 * class with added methods
 */
public class WrappedProject extends ProjectDelegate {
	public WrappedProject(Project project) {
		super(project);
	}
	
	@Override
	public void afterEvaluate(Action<? super Project> action) {
		delegate.afterEvaluate(new Action<Project>() {
			@Override
			public void execute(Project project) {
				WrapperPlugin.instance.invoker.execute(action, project); //invoke under the classpath
			}
		});
	}
}
