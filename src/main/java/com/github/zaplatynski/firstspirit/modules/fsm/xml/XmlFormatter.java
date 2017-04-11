package com.github.zaplatynski.firstspirit.modules.fsm.xml;

import org.apache.maven.plugin.MojoFailureException;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Objects;


/**
 * The type XmlFormatter pretty prints a xml file.
 */
public class XmlFormatter {

  private final File moduleXml;

  /**
   * Instantiates a new XmlFormatter.
   *
   * @param moduleXml the module xml
   */
  public XmlFormatter(final File moduleXml) {
    this.moduleXml = Objects.requireNonNull(moduleXml, "module.xml file is null!");
  }

  /**
   * Pretty print object Document to file.
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
    final LSSerializer lsSerializer = impl.createLSSerializer();

    lsSerializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
    lsSerializer.getDomConfig().setParameter("xml-declaration", Boolean.TRUE);

    LSOutput lsOutput = impl.createLSOutput();
    lsOutput.setEncoding("UTF-8");
    Writer stringWriter = new StringWriter();
    lsOutput.setCharacterStream(stringWriter);
    lsSerializer.write(document, lsOutput);

    try (RandomAccessFile raf = new RandomAccessFile(moduleXml, "rw")) {
      final String newXmlString = stringWriter.toString();
      raf.setLength(newXmlString.getBytes().length);
      raf.writeBytes(newXmlString);
    } catch (IOException e) {
      throw new MojoFailureException(e.getMessage(), e);
    }
  }
}


