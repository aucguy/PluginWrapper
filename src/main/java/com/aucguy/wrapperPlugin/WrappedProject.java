package com.aucguy.wrapperPlugin;

import org.gradle.api.Action;
import org.gradle.api.Project;

import com.aucguy.wrapperPlugin.util.ProjectDelegate;

/**
 * This is a project that overrides methods of another project via delegation.
 */
public class WrappedProject extends ProjectDelegate {
	public WrappedProject(Project project) {
		super(project);
	}
	
	/**
	 * This executes the action after evalution but under the wrapped classpath
	 */
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
