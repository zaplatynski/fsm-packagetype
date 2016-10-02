package com.github.zaplatynski.firstspirit.modules.fsm;

import com.github.zaplatynski.firstspirit.modules.fsm.velocity.VelocityManager;
import com.github.zaplatynski.firstspirit.modules.fsm.xml.XmlValidator;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.AbstractMojoExecutionException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Mojo(name = "moduleXml", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, threadSafe = true)
public class ModuleXmlMojo extends AbstractMojo {

  @Parameter(defaultValue = "/src/main/fsm/module.vm", required = true)
  private String source;

  @Parameter(defaultValue = "${project.build.directory}/module.xml", required = true)
  private File target;

  @Parameter(defaultValue = "true", required = true)
  protected boolean checkXml;

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
      getLog().warn("The source is null. Skip execution!");
      return;
    }

    final File moduleVm = new File(project.getBasedir(), source);
    if (moduleVm.exists()) {

      if (target == null) {
        throw new MojoFailureException(this, "The target is null", "");
      }

      resolveAllTransitiveDependencies();

      try (FileWriter fileWriter = setupTargetFile()) {
        final String templatePath = project.getBasedir().getName() + source;
        VelocityManager velocity = new VelocityManager(project);
        velocity.renderModuleXml(fileWriter, templatePath);
      } catch (MojoExecutionException | MojoFailureException e) {
        throw e;
      } catch (Exception e) {
        throw new MojoExecutionException(e.getMessage(), e);
      }

      if (checkXml) {
        XmlValidator xmlValidator = new XmlValidator(getLog());
        xmlValidator.checkModuleXml(target);
      }

    } else {
      getLog().warn("The source file module.vm does not exist at '"
          + moduleVm.getAbsolutePath() + "'. Skip execution!");
    }
  }

  private FileWriter setupTargetFile() throws AbstractMojoExecutionException, IOException {
    if (!target.exists()) {
      target.getParentFile().mkdirs();

      final boolean fileCreated;
      fileCreated = target.createNewFile();
      if (!fileCreated) {
        throw new MojoFailureException("Could not create target file '"
            + target.getAbsolutePath() + "'!");
      }
    }
    return new FileWriter(target);
  }

  private void resolveAllTransitiveDependencies() {
    final ArtifactResolutionResult resolutionResult = resolveArtifacts();
    project.setDependencyArtifacts(resolutionResult.getArtifacts());
  }

  private ArtifactResolutionResult resolveArtifacts() {
    ArtifactResolutionRequest request = createResolutionRequest();
    return resolver.resolve(request);
  }

  private ArtifactResolutionRequest createResolutionRequest() {
    ArtifactResolutionRequest request = new ArtifactResolutionRequest();
    request.setArtifact(project.getArtifact());
    request.setLocalRepository(local);
    request.setRemoteRepositories(remoteRepos);
    request.setResolveTransitively(true);
    return request;
  }
}
