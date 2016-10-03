package com.github.zaplatynski.firstspirit.modules.fsm;

import com.github.zaplatynski.firstspirit.modules.fsm.xml.XmlFormatter;
import com.github.zaplatynski.firstspirit.modules.fsm.xml.XmlValidator;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.w3c.dom.Document;

import java.util.Optional;

/**
 * The type ModuleXmlMojo is used to parse a Velocity macro file. The module.vm creates a
 * module.xml file with XML validation.
 */
@Mojo(name = "moduleXml", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, threadSafe = true,
    requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class ModuleXmlMojo extends FragmentModuleXmlMojo {

  @Parameter(defaultValue = "true", required = true)
  private boolean checkXml;

  @Parameter(defaultValue = "true", required = true)
  private boolean prettyPrintXml;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    // Render module.xml
    super.execute();
    // Do additional work
    if (moduleVmExists && checkXml) {
      XmlValidator xmlValidator = new XmlValidator();
      final Optional<Document> document = xmlValidator.checkModuleXml(target);
      if (prettyPrintXml && document.isPresent()) {
        XmlFormatter xmlFormatter = new XmlFormatter(target);
        xmlFormatter.prettyPrint(document.get());
      }
    }
  }
}
