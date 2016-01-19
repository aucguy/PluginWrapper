package com.aucguy.wrapperPlugin.util;

import java.io.File;

import org.gradle.api.artifacts.repositories.MavenArtifactRepository;

/**
 * This represents a maven coordinate. A maven coordinate contains the groupId,
 * artifactId and version of an artifact. They are written as something like,
 * "net.minecraftforge.gradle:ForgeGradle:2.0.2" Currently classifier and packaging
 * are not supported
 */
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
	
	/**
	 * Returns the path to the artifact.
	 * @param repository the maven repository the artifact is in
	 * @param packaging the extension or packaging of the artifact
	 * @return the path to the artifact
	 */
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
