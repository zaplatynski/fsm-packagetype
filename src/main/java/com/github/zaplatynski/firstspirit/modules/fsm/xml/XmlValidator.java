package com.github.zaplatynski.firstspirit.modules.fsm.xml;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * The type XmlValidator checks if a XML is well-formed and pretty prints it.
 */
public class XmlValidator {

  /**
   * Check module xml.
   *
   * @param moduleXml the module xml
   * @throws MojoFailureException   the mojo failure exception
   * @throws MojoExecutionException the mojo execution exception
   */
  public Optional<Document> checkModuleXml(File moduleXml) throws MojoFailureException,
  MojoExecutionException {
    DocumentBuilderFactory factory = createDocumentBuilderFactory();
    DocumentBuilder builder = createDocumentBuilder(factory);

    if (builder != null) {
      final Document document = checkForWellFormedness(moduleXml, builder);
      return Optional.ofNullable(document);
    }
    return Optional.empty();

  }

  private static DocumentBuilder createDocumentBuilder(final DocumentBuilderFactory factory)
      throws MojoFailureException {
    DocumentBuilder builder = null;
    try {
      builder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new MojoFailureException(e.getMessage(), e);
    }
    return builder;
  }

  private static DocumentBuilderFactory createDocumentBuilderFactory() {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(false);
    factory.setNamespaceAware(true);
    return factory;
  }

  private static Document checkForWellFormedness(final File moduleXml, final DocumentBuilder
      builder)
      throws MojoExecutionException, MojoFailureException {
    Document document;
    try (FileInputStream moduleXmlStream = new FileInputStream(moduleXml)) {
      document = builder.parse(new InputSource(moduleXmlStream));

    } catch (SAXException e) {
      throw new MojoExecutionException(moduleXml.getAbsoluteFile(), "The file '"
          + moduleXml.getAbsolutePath() + "' is not welll-formed!", e.toString());
    } catch (IOException e) {
      throw new MojoFailureException(e.getMessage(), e);
    }
    return document;
  }

}
