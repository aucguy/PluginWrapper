package com.aucguy.wrapperPlugin.util;

/**
 * for messed up extensions
 */
public class ConfigException extends RuntimeException {
	private static final long serialVersionUID = 6114824742755758071L;

	public ConfigException(String property) {
		super("property wrapper." + property + " is invalid");
	}
	
	public static void raise(String property) {
		throw(new ConfigException(property));
	}
	
	public static void raiseIfNull(Object obj, String property) {
		if(obj == null) raise(property);
	}
}
