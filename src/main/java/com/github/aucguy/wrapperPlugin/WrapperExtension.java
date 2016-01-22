package com.github.aucguy.wrapperPlugin;

/**
 * This holds user specified valeus from build.gradle.
 */
public class WrapperExtension {
	public static final String EXTENSION = "wrapper"; //name of the extension
	
	private String classpath; //binaries of the plugin
	private String plugin; //id of the plugin to load
	private String pom; //maven coordinate of the already built plugin
	private String extraDep; //maven coordinates of dependencies not in the pom file
	private boolean pause = false; //whether to pause or not
	
	public String getClasspath() {
		return classpath;
	}
	
	public void setClasspath(String x) {
		classpath = x;
	}
	
	public String getPlugin() {
		return plugin;
	}
	
	public void setPlugin(String x) {
		plugin = x;
	}
	
	public String getPom() {
		return pom;
	}
	
	public void setPom(String x) {
		pom = x;
	}
	
	public String getExtraDep() {
		return extraDep;
	}
	
	public void setExtraDep(String x) {
		extraDep = x;
	}
	
	public boolean getPause() {
		return pause;
	}
	
	public void setPause(boolean x) {
		pause = x;
	}
}
