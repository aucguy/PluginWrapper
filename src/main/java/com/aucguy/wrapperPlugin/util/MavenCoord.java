package com.aucguy.wrapperPlugin.util;

import java.io.File;

import org.gradle.api.artifacts.repositories.MavenArtifactRepository;

public class MavenCoord {
	private static final String ARTIFACT_FORMAT = "{url}/{group}/{artifact}/{version}/{artifact}-{version}.{packaging}";
	
	public String groupId;
	public String artifactId;
	public String version;
	
	public MavenCoord(String id) {
		String[] parts = id.split(":");
		groupId = parts[0];
		artifactId = parts[1];
		version = parts[2];
	}
	
	public File getPath(MavenArtifactRepository repository, String packaging) {
		String path = ARTIFACT_FORMAT
				.replace("{url}", repository.getUrl().getPath())
				.replace("{group}", groupId.replace('.', '/'))
				.replace("{artifact}", artifactId)
				.replace("{version}", version)
				.replace("{packaging}", packaging);
		return new File(path);
	}
	
	public File getPomPath(MavenArtifactRepository repository) {
		return getPath(repository, "pom");
	}
	

	public File getJarPath(MavenArtifactRepository repository) {
		return getPath(repository, "jar");
	}
	
	@Override
	public String toString() {
		return groupId + ":" + artifactId + ":" + version;
	}
}
