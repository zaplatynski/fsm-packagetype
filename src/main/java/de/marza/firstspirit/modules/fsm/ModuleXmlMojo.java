package de.marza.firstspirit.modules.fsm;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Mojo(name = "moduleXml", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, threadSafe = true)
public class ModuleXmlMojo extends AbstractMojo {

  @Parameter(defaultValue = "/src/main/fsm/module.vm", required = true)
  private String source;

  @Parameter(defaultValue = "${project.build.directory}/module.xml", required = true)
  private File target;

  @Parameter(defaultValue = "${localRepository}", readonly = true, required = true)
  private ArtifactRepository local;

  @Parameter(defaultValue = "${project.remoteArtifactRepositories}", readonly = true,
      required = true)
  protected List<ArtifactRepository> remoteRepos;

  @Component
  private MavenProject project;

  @Component
  protected ArtifactResolver resolver;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {

    if (source == null) {
      throw new MojoFailureException(this, "The source is null", "");
    }

    final File moduleVm = new File(project.getBasedir(), source);
    if (!moduleVm.exists()) {
      throw new MojoFailureException(this, "The source does not exist!", "The file module.vm does"
          + " not exist at '" + moduleVm.getAbsolutePath() + "'!");
    }

    if (target == null) {
      throw new MojoFailureException(this, "The target is null", "");
    }

    final ArtifactResolutionResult resolutionResult = resolveArtifacts();
    project.setDependencyArtifacts(resolutionResult.getArtifacts());

    VelocityEngine velocityEngine = setUpVelocity();
    VelocityContext context = setupVelocityContext();
    setupTargetFile();

    try (FileWriter fileWriter = new FileWriter(target)) {
      final String templatePath = project.getBasedir().getName() + source;
      final Template moduleTemplate = velocityEngine.getTemplate(templatePath);
      moduleTemplate.merge(context, fileWriter);
    } catch (Exception e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

  private void setupTargetFile() throws MojoExecutionException, MojoFailureException {
    if (!target.exists()) {
      target.getParentFile().mkdirs();

      final boolean fileCreated;
      try {
        fileCreated = target.createNewFile();
      } catch (IOException e) {
        throw new MojoExecutionException(e.getMessage(), e);
      }
      if (!fileCreated) {
        throw new MojoFailureException("Could not create target file '"
            + target.getAbsolutePath() + "'!");
      }
    }
  }

  private VelocityContext setupVelocityContext() {
    VelocityContext context = new VelocityContext();
    context.put("project", project);

    for (String property : project.getProperties().stringPropertyNames()) {
      context.put(property, project.getProperties().getProperty(property));
    }

    return context;
  }

  private VelocityEngine setUpVelocity() {
    Properties config = new Properties();
    config.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath,file");
    config.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
        project.getParent().getBasedir().getAbsolutePath());
    config.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
    config.setProperty("eventhandler.include.class", OptionalIncludeEventHandler.class.getName());
    config.setProperty("velocimacro.library", "macros.vm");
    VelocityEngine velocityEngine = new VelocityEngine();
    velocityEngine.init(config);
    return velocityEngine;
  }

  private ArtifactResolutionResult resolveArtifacts() {
    ArtifactResolutionRequest request = new ArtifactResolutionRequest();
    request.setArtifact(project.getArtifact());
    request.setLocalRepository(local);
    request.setRemoteRepositories(remoteRepos);
    request.setResolveTransitively(true);
    return resolver.resolve(request);
  }
}
