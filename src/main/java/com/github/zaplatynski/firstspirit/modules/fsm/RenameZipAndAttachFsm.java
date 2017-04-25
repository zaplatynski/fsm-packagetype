package com.github.zaplatynski.firstspirit.modules.fsm;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Objects;


/**
 * The type Rename and attach fsm.
 */
public class RenameZipAndAttachFsm {

  private final MavenProject project;
  private final File source;
  private final File target;

  /**
   * Instantiates a new Rename zip to fsm.
   *
   * @param project the project
   * @param source  the source
   * @param target  the target
   */
  public RenameZipAndAttachFsm(final MavenProject project, final File source, final File target) {
    this.project = Objects.requireNonNull(project,"Maven project can not be null");
    this.source = Objects.requireNonNull(source,"Source file can not be null");
    this.target = Objects.requireNonNull(target,"Target file can not be null");
  }

  /**
   * Engage.
   *
   * @throws MojoExecutionException the mojo execution exception
   * @throws MojoFailureException   the mojo failure exception
   */
  public void perform() throws MojoExecutionException, MojoFailureException {

    if (source.isDirectory()) {
      throw new MojoFailureException(source, "Source is a directory!", "The source should be a zip "
          + "file and not a directory! -> " + source.getAbsolutePath());
    }

    if (target.exists()) {
      final boolean deleteFailed = !target.delete();
      if (deleteFailed) {
        throw new MojoFailureException(target, "Target deletion prior to rename failed!", "The "
            + "target file exists and could not be deleted before renaming! -> "
            + target.getAbsolutePath());
      }
    }

    final boolean success;
    try {
      success = source.renameTo(target);
    } catch (final Exception error) {
      throw new MojoExecutionException("Renaming to *.fsm failed!", error);
    }

    if (success) {
      project.getArtifact().setFile(target);
    } else {
      throw new MojoFailureException(source, "Renaming to *.fsm failed!", "The reason is unkown. "
          + "See return type in JavaDoc of java.util.File::renameTo() for more details.");
    }
  }
}
