package com.github.aucguy.wrapperPlugin.util;

import java.io.File;
import java.net.URI;

import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.initialization.dsl.ScriptHandler;

import groovy.lang.Closure;

/**
 * This is a delegating script handler. This was generated by eclipse, so don't
 * edit this manually
 */
@SuppressWarnings("rawtypes")
public class ScriptHandlerDelegate implements ScriptHandler {
	public ScriptHandler delegate;

	public ScriptHandlerDelegate(ScriptHandler x) {
		delegate = x;
	}
	
	public void dependencies(Closure arg0) {
		delegate.dependencies(arg0);
	}

	public ClassLoader getClassLoader() {
		return delegate.getClassLoader();
	}

	public ConfigurationContainer getConfigurations() {
		return delegate.getConfigurations();
	}

	public DependencyHandler getDependencies() {
		return delegate.getDependencies();
	}

	public RepositoryHandler getRepositories() {
		return delegate.getRepositories();
	}

	public File getSourceFile() {
		return delegate.getSourceFile();
	}

	public URI getSourceURI() {
		return delegate.getSourceURI();
	}

	public void repositories(Closure arg0) {
		delegate.repositories(arg0);
	}
}
