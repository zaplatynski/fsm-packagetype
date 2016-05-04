package de.marza.firstspirit.modules.fsm;

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

  @Parameter(defaultValue = "${project.build.directory}${project.build.finalName}.fsm",
      required = true)
  private File target;

  @Component
  private MavenProject project;

  /**
   * Run mojo.
   */
  public void execute() throws MojoExecutionException, MojoFailureException {

    if (source == null) {
      throw new MojoFailureException("source is null");
    }

    if (target == null) {
      throw new MojoFailureException("target is null");
    }

    if (!project.getAttachedArtifacts().isEmpty()) {
      throw new MojoFailureException("Configure the maven assembly plugin not to attach no files!");
    }

    getLog().info("Renaming " + source.getName() + " to " + target.getName() + "...");

    final boolean success;
    try {
      success = source.renameTo(target);
    } catch (final Exception e) {
      getLog().error(e);
      throw new MojoExecutionException("Renaming to *.fsm failed: " + e.toString(), e);
    }

    if (success) {
      getLog().info("Success! Attach artifact...");
      project.getArtifact().setFile(target);
    } else {
      getLog().error("Renaming to *.fsm failed (reason unkown)");
      throw new MojoFailureException("Renaming to *.fsm failed (reason unkown)");
    }
  }
}
