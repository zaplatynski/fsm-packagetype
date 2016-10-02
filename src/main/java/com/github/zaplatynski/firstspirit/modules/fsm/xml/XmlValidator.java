package com.github.zaplatynski.firstspirit.modules.fsm.xml;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * The type Xml validator.
 */
public class XmlValidator {

  private final Log log;

  /**
   * Instantiates a new Xml validator.
   *
   * @param log the log
   */
  public XmlValidator(final Log log) {
    this.log = log;
  }

  /**
   * Check module xml.
   *
   * @param moduleXml the module xml
   * @throws MojoFailureException   the mojo failure exception
   * @throws MojoExecutionException the mojo execution exception
   */
  public void checkModuleXml(File moduleXml) throws MojoFailureException, MojoExecutionException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(false);
    factory.setNamespaceAware(true);

    DocumentBuilder builder = null;
    try {
      builder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new MojoFailureException(e.getMessage(), e);
    }

    builder.setErrorHandler(new ModuleXmlErrorHandler(log));
    // the "parse" method also validates XML, will throw an exception if misformatted
    try (FileInputStream moduleXmlStream = new FileInputStream(moduleXml)) {
      final Document document = builder.parse(new InputSource(moduleXmlStream));

      final DOMImplementationRegistry registry;
      try {
        registry = DOMImplementationRegistry.newInstance();
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
        throw new MojoFailureException(e.getMessage(), e);
      }
      final DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
      final LSSerializer writer = impl.createLSSerializer();

      writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
      writer.getDomConfig().setParameter("xml-declaration", Boolean.FALSE);

      RandomAccessFile raf = new RandomAccessFile(moduleXml, "rw");
      final String newXmlString = writer.writeToString(document);
      raf.setLength(newXmlString.getBytes().length);
      raf.writeBytes(newXmlString);
      raf.close();

    } catch (SAXException e) {
      throw new MojoExecutionException(moduleXml.getAbsoluteFile(), "The file '"
           + moduleXml.getAbsolutePath() + "' is not welll-formed!", e.toString());
    } catch (IOException e) {
      throw new MojoFailureException(e.getMessage(), e);
    }

  }

}
