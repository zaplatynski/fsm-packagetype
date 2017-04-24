package com.github.zaplatynski.firstspirit.modules.fsm.velocity;

import org.apache.maven.plugin.AbstractMojoExecutionException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;


/**
 * The type Module xml parser is the entry class to parse a Velocity macro to a module xml.
 */
public class ModuleXmlParser {

  private final String source;
  private final File target;
  private final MavenProject project;
  private final Log log;
  private final VelocityManager velocity;
  private boolean moduleVmExistent;

  /**
   * Instantiates a new Module xml parser.
   *
   * @param source  the source
   * @param target  the target
   * @param project the project
   * @param log     the log
   */
  public ModuleXmlParser(final String source, final File target, final MavenProject project,
                         final Log log) {
    this(source,target,project, log, new VelocityManager(project));
  }

  /**
   * Instantiates a new Module xml parser. Used in tests.
   *
   * @param source   the source
   * @param target   the target
   * @param project  the project
   * @param log      the log
   * @param velocity the velocity
   */
  public ModuleXmlParser(final String source, final File target, final MavenProject project,
                         final Log log, final VelocityManager velocity) {
    this.source = Objects.requireNonNull(source);
    this.target = Objects.requireNonNull(target);
    this.project = Objects.requireNonNull(project);
    this.log = Objects.requireNonNull(log);
    this.velocity = Objects.requireNonNull(velocity);
  }

  /**
   * Parse module vm.
   *
   * @throws MojoExecutionException the mojo execution exception
   * @throws MojoFailureException   the mojo failure exception
   */
  public void parseModuleVm() throws MojoExecutionException, MojoFailureException {
    final File moduleVm = new File(project.getBasedir(), source);

    moduleVmExistent = moduleVm.exists();
    if (moduleVmExistent) {

      try (FileWriter fileWriter = setupTargetFile()) {
        final String templatePath = project.getBasedir().getName() + source;
        velocity.renderModuleXml(fileWriter, templatePath);
      } catch (Exception e) {
        throw new MojoExecutionException(e.getMessage(), e);
      }

    } else {
      throw new MojoExecutionException(moduleVm,"Velocity macro " + moduleVm.getName() + " is " +
          "missing","The velocity macro does not exist at '"
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

  /**
   * Is module.vm existent?
   *
   * @return the boolean
   */
  public boolean isModuleVmExistent() {
    return moduleVmExistent;
  }
}
