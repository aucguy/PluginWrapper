package com.github.aucguy.wrapperPlugin;

import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.initialization.dsl.ScriptHandler;

import com.github.aucguy.wrapperPlugin.util.ScriptHandlerDelegate;

public class WrappedScriptHandler extends ScriptHandlerDelegate {
	public WrappedScriptHandler(ScriptHandler scripthandler) {
		super(scripthandler);
	}
	
	@Override
	public ConfigurationContainer getConfigurations() {
		return new WrappedConfigurationContainer(super.getConfigurations());
	}
}
