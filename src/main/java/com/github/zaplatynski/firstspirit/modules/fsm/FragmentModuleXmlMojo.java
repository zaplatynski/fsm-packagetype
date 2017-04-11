package com.github.zaplatynski.firstspirit.modules.fsm;

import com.github.zaplatynski.firstspirit.modules.fsm.velocity.ModuleXmlParser;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;

/**
 * The type FragmentModuleXmlMojo is used to parse a Velocity macro file. The module.vm creates a
 * module.xml file without XML validation.
 */
@Mojo(name = "fragmentModuleXml", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, threadSafe =
    true,requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class FragmentModuleXmlMojo extends AbstractMojo {

  @Parameter(defaultValue = "/src/main/fsm/module-fragment.vm", required = true)
  private String source;

  @Parameter(defaultValue = "${project.build.directory}/module-fragment.xml", required = true)
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

    if (target == null) {
      throw new MojoFailureException(this, "The target is null!", "The target path for the "
          + "module-fragment.xml is null. Please provide a target path.");
    }

    ModuleXmlParser parser = new ModuleXmlParser(source,target,project,getLog());
    parser.parseModuleVm();
  }


}
