package com.github.zaplatynski.firstspirit.modules.fsm.velocity;

import org.apache.maven.project.MavenProject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.Writer;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;


/**
 * The type VVelocityManager encapsulates the Velocity engine to parse macros.
 */
public class VelocityManager {


  public static final String UTF_8 = "UTF-8";
  private final VelocityEngine velocityEngine;
  private final VelocityContext context;

  /**
   * Instantiates a new Velocity manager.
   *
   * @param project the project
   */
  public VelocityManager(final MavenProject project) {
    Objects.requireNonNull(project, "Maven project is null");
    velocityEngine = setUpVelocity(project);
    context = setupVelocityContext(project);
  }

  /**
   * Render module xml.
   *
   * @param writer       the file writer
   * @param templatePath the template path
   */
  public void renderModuleXml(final Writer writer, final String templatePath) {
    final Template moduleTemplate = velocityEngine.getTemplate(templatePath);
    moduleTemplate.merge(context, writer);
  }

  private static VelocityContext setupVelocityContext(final MavenProject project) {
    VelocityContext context = new VelocityContext();

    // add Maven project to context
    context.put("project", project);

    // add all Maven properties to context
    final Properties projectProperties = project.getProperties();
    for (Object key : projectProperties.keySet()) {
      context.put(key.toString(), projectProperties.get(key));
    }

    return context;
  }

  private static VelocityEngine setUpVelocity(final MavenProject project) {
    final String templateRoot = getTemplateRoot(project);

    final Properties config = new Properties();

    config.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath,file");
    config.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, templateRoot);
    config.setProperty(RuntimeConstants.VM_LIBRARY, "macros.vm");
    config.setProperty(RuntimeConstants.ENCODING_DEFAULT, UTF_8);
    config.setProperty(RuntimeConstants.INPUT_ENCODING, UTF_8);
    config.setProperty(RuntimeConstants.OUTPUT_ENCODING, UTF_8);

    final String resourceLoader = ClasspathResourceLoader.class.getName();
    config.setProperty("classpath.resource.loader.class", resourceLoader);

    final String includeHandler = OptionalIncludeEventHandler.class.getName();
    config.setProperty(RuntimeConstants.EVENTHANDLER_INCLUDE, includeHandler);


    VelocityEngine velocityEngine = new VelocityEngine();
    velocityEngine.init(config);
    return velocityEngine;
  }

  private static String getTemplateRoot(final MavenProject project) {
    if (project.getParent() != null && isNotFlatProject(project)) {
        return project.getParent().getBasedir().getAbsolutePath();
    }
    return project.getBasedir().getParentFile().getAbsolutePath();
  }

  private static boolean isNotFlatProject(MavenProject project) {
    Path basedir = project.getBasedir().toPath();
    Path parentDir = project.getParent().getBasedir().toPath();
    return basedir.startsWith(parentDir);
  }
}
