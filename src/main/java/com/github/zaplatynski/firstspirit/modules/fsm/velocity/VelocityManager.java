package com.github.zaplatynski.firstspirit.modules.fsm.velocity;

import org.apache.maven.project.MavenProject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.Writer;
import java.util.Properties;


/**
 * The type Velocity manager.
 */
public class VelocityManager {


  private final VelocityEngine velocityEngine;
  private final VelocityContext context;

  /**
   * Instantiates a new Velocity manager.
   *
   * @param project the project
   */
  public VelocityManager(final MavenProject project) {
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
    for (String property : project.getProperties().stringPropertyNames()) {
      context.put(property, project.getProperties().getProperty(property));
    }

    return context;
  }

  private static VelocityEngine setUpVelocity(final MavenProject project) {
    final String templateRoot = getTemplateRoot(project);

    final Properties config = new Properties();

    config.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath,file");
    config.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, templateRoot);
    config.setProperty(RuntimeConstants.VM_LIBRARY, "macros.vm");

    final String resourceLoader = ClasspathResourceLoader.class.getName();
    config.setProperty("classpath.resource.loader.class", resourceLoader);

    final String includeHandler = OptionalIncludeEventHandler.class.getName();
    config.setProperty(RuntimeConstants.EVENTHANDLER_INCLUDE, includeHandler);


    VelocityEngine velocityEngine = new VelocityEngine();
    velocityEngine.init(config);
    return velocityEngine;
  }

  private static String getTemplateRoot(final MavenProject project) {
    if (project.getParent() != null) {
      return project.getParent().getBasedir().getAbsolutePath();
    }
    return project.getBasedir().getAbsolutePath();
  }
}
