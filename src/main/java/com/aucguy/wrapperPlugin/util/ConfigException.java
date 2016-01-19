package com.aucguy.wrapperPlugin.util;

/**
 * This is thrown for messed up extensions
 */
public class ConfigException extends RuntimeException {
	private static final long serialVersionUID = 6114824742755758071L;
	
	/**
	 * This creates a {@link #ConfigException}
	 * @param property the name of the extension property
	 */
	public ConfigException(String property) {
		super("property wrapper." + property + " is invalid"); //special message
	}
	
	public static void raise(String property) {
		throw(new ConfigException(property));
	}
	
	public static void raiseIfNull(Object obj, String property) {
		if(obj == null) raise(property);
	}
}
