package com.github.zaplatynski.firstspirit.modules.fsm;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.needle4j.annotation.InjectInto;
import org.needle4j.annotation.Mock;
import org.needle4j.annotation.ObjectUnderTest;
import org.needle4j.junit.NeedleBuilders;
import org.needle4j.junit.NeedleRule;

import java.io.File;
import java.util.Arrays;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by marza on 24.04.17.
 */
public class FsmMojoTest {

  @Rule
  public NeedleRule needleRule = NeedleBuilders
      .needleMockitoRule()
      .build();

  @Mock
  @InjectInto(targetComponentId = "testling", fieldName = "project")
  private MavenProject project;

  @InjectInto(targetComponentId = "testling", fieldName = "source")
  private File source = new File("target/module.zip");

  @InjectInto(targetComponentId = "testling", fieldName = "target")
  private File target = new File("target/module.fsm");

  @Mock
  private Artifact artifact;

  @ObjectUnderTest(id = "testling")
  private FsmMojo testling = new FsmMojo();

  @Before
  public void setUp() throws Exception {
    source.createNewFile();
  }

  @After
  public void tearDown() throws Exception {
    if(target.exists()) {
      target.delete();
    }
    if(source.exists()) {
      source.delete();
    }
  }

  @Test
  public void execute() throws Exception {

    when(project.getArtifact()).thenReturn(artifact);

    assertThat(source.exists(), is(TRUE));
    assertThat(target.exists(), is(FALSE));

    testling.execute();

    assertThat(source.exists(), is(FALSE));
    assertThat(target.exists(), is(TRUE));

    verify(artifact).setFile(target);
  }

  @Test(expected = MojoFailureException.class)
  public void executeAlreadyAttached() throws Exception {
    when(project.getAttachedArtifacts()).thenReturn(Arrays.asList(artifact));

    testling.execute();
  }


}