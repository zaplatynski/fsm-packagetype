package com.github.zaplatynski.firstspirit.modules.fsm;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.needle4j.annotation.InjectInto;
import org.needle4j.annotation.Mock;
import org.needle4j.annotation.ObjectUnderTest;
import org.needle4j.junit.NeedleBuilders;
import org.needle4j.junit.NeedleRule;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static org.mockito.Mockito.when;

public class ModuleXmlMojoTest {

  @Rule
  public NeedleRule needleRule = NeedleBuilders
      .needleMockitoRule()
      .build();

  @Mock
  @InjectInto(targetComponentId = "testling", fieldName = "project")
  private MavenProject project;

  @InjectInto(targetComponentId = "testling", fieldName = "source")
  private String source = "/module.vm";

  @InjectInto(targetComponentId = "testling", fieldName = "target")
  private File target = new File("target/module.xml");

  @InjectInto(targetComponentId = "testling", fieldName = "checkXml")
  private boolean checkXml = true;

  @InjectInto(targetComponentId = "testling", fieldName = "prettyPrintXml")
  private boolean prettyPrintXml = true;

  @ObjectUnderTest(id = "testling")
  private ModuleXmlMojo testling = new ModuleXmlMojo();

  private ModuleXmlMojo testlingMissingParameters = new ModuleXmlMojo();

  public ModuleXmlMojoTest() throws IOException {
  }

  @Before
  public void setUp() throws Exception {
    target.createNewFile();
  }

  @Test
  public void execute() throws Exception {

    when(project.getArtifactId()).thenReturn("artifact");
    when(project.getName()).thenReturn("My FSM");
    when(project.getVersion()).thenReturn("1.0");
    when(project.getProperties()).thenReturn(new Properties());
    when(project.getBasedir()).thenReturn(new File("src/test/resources/"));

    testling.execute();
  }

  @Test(expected = MojoFailureException.class)
  public void executeWithErrors() throws Exception {
    testlingMissingParameters.execute();
  }

}