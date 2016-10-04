package com.github.zaplatynski.firstspirit.modules.fsm.xml;

import org.junit.After;
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
import static org.junit.Assert.assertThat;


public class XmlFormatterTest {

  @Rule
  public TemporaryFolder temporary = new TemporaryFolder();

  private XmlValidator validator;
  private File moduleXml;
  private XmlFormatter testling;
  private RandomAccessFile accessModuleXml;

  @Before
  public void setUp() throws Exception {
    validator = new XmlValidator();

    moduleXml = temporary.newFile();

    testling = new XmlFormatter(moduleXml);

    accessModuleXml = new RandomAccessFile(moduleXml, "rw");
  }

  @After
  public void tearDown() throws Exception {
    if(accessModuleXml != null) {
      accessModuleXml.close();
    }
  }

  @Test
  public void prettyPrint() throws Exception {

    final String xmlData = "<module><name>test</name></module>";
    writeFileContents(xmlData);

    final Optional<Document> document = validator.checkModuleXml(moduleXml);

    testling.prettyPrint(document.get());


    final String content = readFileContents();

    assertThat(content, is(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<module>\n" +
            "    <name>test</name>\n" +
            "</module>\n"));
  }

   private void writeFileContents(final String xmlData) throws IOException {
    accessModuleXml.setLength(xmlData.getBytes().length);
    accessModuleXml.writeBytes(xmlData);
    accessModuleXml.close();
  }

  private String readFileContents() throws IOException {
    accessModuleXml = new RandomAccessFile(moduleXml, "r");
    StringBuilder buffer = new StringBuilder();
    String line;
    while((line = accessModuleXml.readLine()) != null){
      buffer.append(line);
      buffer.append("\n");
    }
    return buffer.toString();
  }

}