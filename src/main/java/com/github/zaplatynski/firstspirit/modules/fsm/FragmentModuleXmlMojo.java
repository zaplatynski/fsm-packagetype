package com.github.zaplatynski.firstspirit.modules.fsm;

import com.github.zaplatynski.firstspirit.modules.fsm.velocity.VelocityManager;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.AbstractMojoExecutionException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The type FragmentModuleXmlMojo is used to parse a Velocity macro file. The module.vm creates a
 * module.xml file without XML validation.
 */
@Mojo(name = "fragmentModuleXml", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, threadSafe =
    true,requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class FragmentModuleXmlMojo extends AbstractMojo {

  @Parameter(defaultValue = "/src/main/fsm/module.vm", required = true)
  private String source;

  @Parameter(defaultValue = "${project.build.directory}/module.xml", required = true)
  protected File target;

  @Component
  private MavenProject project;

  protected boolean moduleVmExists;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {

    if (source == null) {
      getLog().warn("The source is null. Skip execution!");
      return;
    }

    final File moduleVm = new File(project.getBasedir(), source);
    moduleVmExists = moduleVm.exists();
    if (moduleVmExists) {

      if (target == null) {
        throw new MojoFailureException(this, "The target is null", "");
      }

      try (FileWriter fileWriter = setupTargetFile()) {
        final String templatePath = project.getBasedir().getName() + source;
        VelocityManager velocity = new VelocityManager(project);
        velocity.renderModuleXml(fileWriter, templatePath);
      } catch (MojoExecutionException | MojoFailureException e) {
        throw e;
      } catch (Exception e) {
        throw new MojoExecutionException(e.getMessage(), e);
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
}
