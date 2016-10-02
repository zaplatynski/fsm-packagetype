package com.github.zaplatynski.firstspirit.modules.fsm;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

/**
 * The type FsmMojo renames a zip to a fsm file.
 */
@Mojo(name = "fsm", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true)
public class FsmMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project.build.directory}/${project.build.finalName}.zip",
      required = true)
  private File source;

  @Parameter(defaultValue = "${project.build.directory}/${project.build.finalName}.fsm",
      required = true)
  private File target;

  @Component
  private MavenProject project;

  /**
   * Run mojo.
   */
  public void execute() throws MojoExecutionException, MojoFailureException {

    if (source == null) {
      throw new MojoFailureException(this, "The source is null", "");
    }

    if (target == null) {
      throw new MojoFailureException(this, "The target is null", "");
    }

    if (!project.getAttachedArtifacts().isEmpty()) {
      throw new MojoFailureException(this, "Assembly attached files!", "Configure the Maven "
          + "assembly plugin to attach any files! -> <attach>false</attach>");
    }

    getLog().info("Try to rename '" + source.getName() + "' to '" + target.getName() + "'...");
    getLog().debug("FSM Source: " + source.getAbsolutePath());
    getLog().debug("FSM Target: " + target.getAbsolutePath());

    try {
      final RenameZipAndAttachFsm zipToFsm = new RenameZipAndAttachFsm(project, source, target);
      zipToFsm.engage();
    } catch (final MojoExecutionException | MojoFailureException mojoException) {
      throw mojoException;
    } catch (final Exception error) {
      throw new MojoExecutionException("An error occurred while renaming!", error);
    }

    getLog().info("Successfully attached FSM as artifact!");
  }

}
