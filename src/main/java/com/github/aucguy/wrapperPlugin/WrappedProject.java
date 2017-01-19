package com.github.aucguy.wrapperPlugin;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.initialization.dsl.ScriptHandler;

import com.github.aucguy.wrapperPlugin.util.ProjectDelegate;

/**
 * This is a project that overrides methods of another project via delegation.
 */
public class WrappedProject extends ProjectDelegate {
	public WrappedProject(Project project) {
		super(project);
	}
	
	/**
	 * This executes the action after evaluation but under the wrapped classpath
	 */
	@Override
	public void afterEvaluate(Action<? super Project> action) {
		super.afterEvaluate(new Action<Project>() {
			@Override
			public void execute(Project project) {
				WrapperPlugin.instance.invoker.execute(action, project); //invoke under the classpath
			}
		});
	}
	
	@Override
	public ScriptHandler getBuildscript() {
		return new WrappedScriptHandler(super.getBuildscript());
	}
}
