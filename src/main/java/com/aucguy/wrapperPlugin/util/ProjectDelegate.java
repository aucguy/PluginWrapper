package com.aucguy.wrapperPlugin.util;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gradle.api.Action;
import org.gradle.api.AntBuilder;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.NamedDomainObjectFactory;
import org.gradle.api.PathValidation;
import org.gradle.api.Project;
import org.gradle.api.ProjectState;
import org.gradle.api.Task;
import org.gradle.api.UnknownProjectException;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.dsl.ArtifactHandler;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.component.SoftwareComponentContainer;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.FileTree;
import org.gradle.api.initialization.dsl.ScriptHandler;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.LoggingManager;
import org.gradle.api.plugins.Convention;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.ObjectConfigurationAction;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.resources.ResourceHandler;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.WorkResult;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;
import org.gradle.process.JavaExecSpec;

import groovy.lang.Closure;
import groovy.lang.MissingPropertyException;

@SuppressWarnings("rawtypes")
public class ProjectDelegate implements Project{
	public Project delegate;
	
	public ProjectDelegate(Project x) {
		delegate = x;
	}
	
	public PluginContainer getPlugins() {
		return delegate.getPlugins();
	}

	public void apply(Closure closure) {
		delegate.apply(closure);
	}

	public void apply(Action<? super ObjectConfigurationAction> action) {
		delegate.apply(action);
	}

	public void apply(Map<String, ?> options) {
		delegate.apply(options);
	}

	public PluginManager getPluginManager() {
		return delegate.getPluginManager();
	}

	public Project getRootProject() {
		return delegate.getRootProject();
	}

	public File getRootDir() {
		return delegate.getRootDir();
	}

	public File getBuildDir() {
		return delegate.getBuildDir();
	}

	public void setBuildDir(Object path) {
		delegate.setBuildDir(path);
	}

	public File getBuildFile() {
		return delegate.getBuildFile();
	}

	public Project getParent() {
		return delegate.getParent();
	}

	public String getName() {
		return delegate.getName();
	}

	public String getDescription() {
		return delegate.getDescription();
	}

	public void setDescription(String description) {
		delegate.setDescription(description);
	}

	public Object getGroup() {
		return delegate.getGroup();
	}

	public void setGroup(Object group) {
		delegate.setGroup(group);
	}

	public Object getVersion() {
		return delegate.getVersion();
	}

	public void setVersion(Object version) {
		delegate.setVersion(version);
	}

	public Object getStatus() {
		return delegate.getStatus();
	}

	public void setStatus(Object status) {
		delegate.setStatus(status);
	}

	public Map<String, Project> getChildProjects() {
		return delegate.getChildProjects();
	}

	public void setProperty(String name, Object value) throws MissingPropertyException {
		delegate.setProperty(name, value);
	}

	public Project getProject() {
		return delegate.getProject();
	}

	public Set<Project> getAllprojects() {
		return delegate.getAllprojects();
	}

	public Set<Project> getSubprojects() {
		return delegate.getSubprojects();
	}

	public Task task(String name) throws InvalidUserDataException {
		return delegate.task(name);
	}

	public Task task(Map<String, ?> args, String name) throws InvalidUserDataException {
		return delegate.task(args, name);
	}

	public Task task(Map<String, ?> args, String name, Closure configureClosure) {
		return delegate.task(args, name, configureClosure);
	}

	public Task task(String name, Closure configureClosure) {
		return delegate.task(name, configureClosure);
	}

	public String getPath() {
		return delegate.getPath();
	}

	public List<String> getDefaultTasks() {
		return delegate.getDefaultTasks();
	}

	public void setDefaultTasks(List<String> defaultTasks) {
		delegate.setDefaultTasks(defaultTasks);
	}

	public void defaultTasks(String... defaultTasks) {
		delegate.defaultTasks(defaultTasks);
	}

	public Project evaluationDependsOn(String path) throws UnknownProjectException {
		return delegate.evaluationDependsOn(path);
	}

	public void evaluationDependsOnChildren() {
		delegate.evaluationDependsOnChildren();
	}

	public Project findProject(String path) {
		return delegate.findProject(path);
	}

	public Project project(String path) throws UnknownProjectException {
		return delegate.project(path);
	}

	public Project project(String path, Closure configureClosure) {
		return delegate.project(path, configureClosure);
	}

	public Map<Project, Set<Task>> getAllTasks(boolean recursive) {
		return delegate.getAllTasks(recursive);
	}

	public Set<Task> getTasksByName(String name, boolean recursive) {
		return delegate.getTasksByName(name, recursive);
	}

	public File getProjectDir() {
		return delegate.getProjectDir();
	}

	public File file(Object path) {
		return delegate.file(path);
	}

	public File file(Object path, PathValidation validation) throws InvalidUserDataException {
		return delegate.file(path, validation);
	}

	public URI uri(Object path) {
		return delegate.uri(path);
	}

	public String relativePath(Object path) {
		return delegate.relativePath(path);
	}

	public ConfigurableFileCollection files(Object... paths) {
		return delegate.files(paths);
	}

	public ConfigurableFileCollection files(Object paths, Closure configureClosure) {
		return delegate.files(paths, configureClosure);
	}

	public ConfigurableFileTree fileTree(Object baseDir) {
		return delegate.fileTree(baseDir);
	}

	public ConfigurableFileTree fileTree(Object baseDir, Closure configureClosure) {
		return delegate.fileTree(baseDir, configureClosure);
	}

	public ConfigurableFileTree fileTree(Map<String, ?> args) {
		return delegate.fileTree(args);
	}

	public FileTree zipTree(Object zipPath) {
		return delegate.zipTree(zipPath);
	}

	public FileTree tarTree(Object tarPath) {
		return delegate.tarTree(tarPath);
	}

	public File mkdir(Object path) {
		return delegate.mkdir(path);
	}

	public boolean delete(Object... paths) {
		return delegate.delete(paths);
	}

	public ExecResult javaexec(Closure closure) {
		return delegate.javaexec(closure);
	}

	public ExecResult javaexec(Action<? super JavaExecSpec> action) {
		return delegate.javaexec(action);
	}

	public ExecResult exec(Closure closure) {
		return delegate.exec(closure);
	}

	public ExecResult exec(Action<? super ExecSpec> action) {
		return delegate.exec(action);
	}

	public String absoluteProjectPath(String path) {
		return delegate.absoluteProjectPath(path);
	}

	public String relativeProjectPath(String path) {
		return delegate.relativeProjectPath(path);
	}

	public AntBuilder getAnt() {
		return delegate.getAnt();
	}

	public AntBuilder createAntBuilder() {
		return delegate.createAntBuilder();
	}

	public AntBuilder ant(Closure configureClosure) {
		return delegate.ant(configureClosure);
	}

	public ConfigurationContainer getConfigurations() {
		return delegate.getConfigurations();
	}

	public void configurations(Closure configureClosure) {
		delegate.configurations(configureClosure);
	}

	public ArtifactHandler getArtifacts() {
		return delegate.getArtifacts();
	}

	public void artifacts(Closure configureClosure) {
		delegate.artifacts(configureClosure);
	}

	public Convention getConvention() {
		return delegate.getConvention();
	}

	public int depthCompare(Project otherProject) {
		return delegate.depthCompare(otherProject);
	}

	public int getDepth() {
		return delegate.getDepth();
	}

	public TaskContainer getTasks() {
		return delegate.getTasks();
	}

	public void subprojects(Action<? super Project> action) {
		delegate.subprojects(action);
	}

	public void subprojects(Closure configureClosure) {
		delegate.subprojects(configureClosure);
	}

	public void allprojects(Action<? super Project> action) {
		delegate.allprojects(action);
	}

	public void allprojects(Closure configureClosure) {
		delegate.allprojects(configureClosure);
	}

	public void beforeEvaluate(Action<? super Project> action) {
		delegate.beforeEvaluate(action);
	}

	public void afterEvaluate(Action<? super Project> action) {
		delegate.afterEvaluate(action);
	}

	public void beforeEvaluate(Closure closure) {
		delegate.beforeEvaluate(closure);
	}

	public void afterEvaluate(Closure closure) {
		delegate.afterEvaluate(closure);
	}

	public boolean hasProperty(String propertyName) {
		return delegate.hasProperty(propertyName);
	}

	public Map<String, ?> getProperties() {
		return delegate.getProperties();
	}

	public Object property(String propertyName) throws MissingPropertyException {
		return delegate.property(propertyName);
	}

	public Logger getLogger() {
		return delegate.getLogger();
	}

	public Gradle getGradle() {
		return delegate.getGradle();
	}

	public LoggingManager getLogging() {
		return delegate.getLogging();
	}

	public Object configure(Object object, Closure configureClosure) {
		return delegate.configure(object, configureClosure);
	}

	public Iterable<?> configure(Iterable<?> objects, Closure configureClosure) {
		return delegate.configure(objects, configureClosure);
	}

	public <T> Iterable<T> configure(Iterable<T> objects, Action<? super T> configureAction) {
		return delegate.configure(objects, configureAction);
	}

	public RepositoryHandler getRepositories() {
		return delegate.getRepositories();
	}

	public void repositories(Closure configureClosure) {
		delegate.repositories(configureClosure);
	}

	public DependencyHandler getDependencies() {
		return delegate.getDependencies();
	}

	public void dependencies(Closure configureClosure) {
		delegate.dependencies(configureClosure);
	}

	public ScriptHandler getBuildscript() {
		return delegate.getBuildscript();
	}

	public void buildscript(Closure configureClosure) {
		delegate.buildscript(configureClosure);
	}

	public WorkResult copy(Closure closure) {
		return delegate.copy(closure);
	}

	public CopySpec copySpec(Closure closure) {
		return delegate.copySpec(closure);
	}

	public WorkResult copy(Action<? super CopySpec> action) {
		return delegate.copy(action);
	}

	public CopySpec copySpec(Action<? super CopySpec> action) {
		return delegate.copySpec(action);
	}

	public CopySpec copySpec() {
		return delegate.copySpec();
	}

	public ProjectState getState() {
		return delegate.getState();
	}

	public <T> NamedDomainObjectContainer<T> container(Class<T> type) {
		return delegate.container(type);
	}

	public <T> NamedDomainObjectContainer<T> container(Class<T> type, NamedDomainObjectFactory<T> factory) {
		return delegate.container(type, factory);
	}

	public <T> NamedDomainObjectContainer<T> container(Class<T> type, Closure factoryClosure) {
		return delegate.container(type, factoryClosure);
	}

	public ExtensionContainer getExtensions() {
		return delegate.getExtensions();
	}

	public ResourceHandler getResources() {
		return delegate.getResources();
	}

	public SoftwareComponentContainer getComponents() {
		return delegate.getComponents();
	}

	@Override
	public int compareTo(Project arg0) {
		return delegate.compareTo(arg0);
	}
}