package com.github.aucguy.wrapperPlugin;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.UnknownConfigurationException;

import com.github.aucguy.wrapperPlugin.util.ConfigurationContainerDelegate;

public class WrappedConfigurationContainer extends ConfigurationContainerDelegate {
	public WrappedConfigurationContainer(ConfigurationContainer x) {
		super(x);
	}
	
	public Configuration getByName(String name) throws UnknownConfigurationException {
		if(name.equals("classpath")) {
			return WrapperPlugin.instance.wrappedProject.getConfigurations().getByName("fakeClasspath");
		}
		return super.getByName(name);
	}
}
