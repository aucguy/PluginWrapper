package com.aucguy.wrapperPlugin.util;

import org.gradle.api.Action;
import org.gradle.api.Project;

/**
 * used to execute things under another classpath
 */
public class Invoker {
	public void execute(Action<? super Project> action, Project project) {
		action.execute(project);
	}
}
