package com.github.zaplatynski.firstspirit.modules.fsm.xml;

import org.apache.maven.plugin.MojoFailureException;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * The type Xml formatter.
 */
public class XmlFormatter {

  private final File moduleXml;

  /**
   * Instantiates a new Xml formatter.
   *
   * @param moduleXml the module xml
   */
  public XmlFormatter(final File moduleXml) {
    this.moduleXml = moduleXml;
  }

  /**
   * Pretty print.
   *
   * @param document the document
   * @throws MojoFailureException the mojo failure exception
   */
  public void prettyPrint(final Document document)
      throws MojoFailureException {

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

    try (RandomAccessFile raf = new RandomAccessFile(moduleXml, "rw")) {
      final String newXmlString = writer.writeToString(document);
      raf.setLength(newXmlString.getBytes().length);
      raf.writeBytes(newXmlString);
    } catch (IOException e) {
      throw new MojoFailureException(e.getMessage(), e);
    }
  }
}


