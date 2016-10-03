package com.github.zaplatynski.firstspirit.modules.fsm.xml;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class XmlValidatorTest {

  @Rule
  public TemporaryFolder temporary = new TemporaryFolder();

  private XmlValidator testling;
  private File moduleXml;
  private RandomAccessFile file;

  @Before
  public void setUp() throws Exception {
    testling = new XmlValidator();

    moduleXml = temporary.newFile();
    file = new RandomAccessFile(moduleXml, "rw");
  }

  @Test
  public void checkModuleXmlSuccess() throws Exception {

    final String validXmlData = "<module><name>test</name></module>";
    writeModuleXmlContent(validXmlData);

    final Optional<Document> document = testling.checkModuleXml(moduleXml);

    assertThat(document, is(notNullValue()));
    assertThat(document.isPresent(), is(Boolean.TRUE));
  }

  @Test(expected = MojoExecutionException.class)
  public void checkModuleXmlFailed() throws Exception {

    final String invalidXmlData = "<module><name></module>";
    writeModuleXmlContent(invalidXmlData);

   testling.checkModuleXml(moduleXml);
  }

  private void writeModuleXmlContent(final String xmlData) throws IOException {
    file.setLength(xmlData.getBytes().length);
    file.writeBytes(xmlData);
    file.close();
  }

}