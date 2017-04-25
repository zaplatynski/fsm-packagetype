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
import java.util.Properties;

import static org.mockito.Mockito.when;

public class FragmentModuleXmlMojoTest {

  @Rule
  public NeedleRule needleRule = NeedleBuilders
      .needleMockitoRule()
      .build();

  @Mock
  @InjectInto(targetComponentId = "testling", fieldName = "project")
  private MavenProject project;

  @InjectInto(targetComponentId = "testling", fieldName = "source")
  private String source = "/module-fragment.vm";

  @InjectInto(targetComponentId = "testlingMissingParameterTarget", fieldName = "source")
  private String source2 = source;

  @InjectInto(targetComponentId = "testling", fieldName = "target")
  private File target = new File("target/module-fragment.xml");

  @ObjectUnderTest(id = "testling")
  private FragmentModuleXmlMojo testling = new FragmentModuleXmlMojo();

  @ObjectUnderTest(id = "testlingMissingParameterTarget")
  private FragmentModuleXmlMojo testlingMissingParameterTarget = new FragmentModuleXmlMojo();

  @ObjectUnderTest
  private FragmentModuleXmlMojo testlingMissingParameters = new FragmentModuleXmlMojo();

  @Before
  public void setUp() throws Exception {
    target.createNewFile();
  }

  @Test
  public void execute() throws Exception {

    when(project.getArtifactId()).thenReturn("artifact");
    when(project.getName()).thenReturn("My FSM");
    when(project.getProperties()).thenReturn(new Properties());
    when(project.getBasedir()).thenReturn(new File("src/test/resources/"));

    testling.execute();
  }

  @Test(expected = MojoFailureException.class)
  public void executeWithMissingSource() throws Exception {
    testlingMissingParameters.execute();
  }

  @Test(expected = MojoFailureException.class)
  public void executeWithMissingTarget() throws Exception {
    testlingMissingParameterTarget.execute();
  }

}