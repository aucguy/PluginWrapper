package com.aucguy.wrapperPlugin;

/**
 * extension class
 */
public class WrapperExtension {
	private String classpath; //classpath to load the plugin under
	private String plugin; //id of the plugin to load
	private String pom; //pom file with dependencies
	private String extraDep; //dependencies not in the pom file
	
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
}
