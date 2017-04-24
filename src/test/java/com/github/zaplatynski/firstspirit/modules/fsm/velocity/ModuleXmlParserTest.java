package com.github.zaplatynski.firstspirit.modules.fsm.velocity;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.File;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class ModuleXmlParserTest {

  @Rule
  public TemporaryFolder tempFiles = new TemporaryFolder();

  @Rule
  public MockitoRule injectMocks = MockitoJUnit.rule();

  @Mock
  private MavenProject project;

  @Mock
  private Log log;

  @Mock
  private VelocityManager velocity;

  private String source;
  private File target;
  private ModuleXmlParser testling;

  @Before
  public void setUp() throws Exception {
    source = "src/main/fsm/module.vm";
    target = new File(tempFiles.newFolder(),"module.xml");
    testling = new ModuleXmlParser(source, target,project, velocity);

    when(project.getBasedir()).thenReturn(tempFiles.newFolder());
  }

  @Test(expected = MojoExecutionException.class)
  public void parseModuleVmModuleVmMissing() throws Exception {

    assertThat("Target must be non-existant", target.exists(), is(FALSE));

    testling.parseModuleVm();
  }

  @Test
  public void parseModuleVm() throws Exception {
    assertThat("Taget must be non-existant", target.exists(), is(FALSE));

    final File moduleXml = new File(project.getBasedir(), source);
    moduleXml.mkdirs();

    testling.parseModuleVm();

    assertThat("Taget must be existant", target.exists(), is(TRUE));
    verify(log,never()).warn(anyString());

    String templatePath = project.getBasedir().getName() + source;
    verify(velocity).renderModuleXml(any(), eq(templatePath));
  }

  @Test(expected = MojoExecutionException.class)
  public void parseModuleVmException() throws Exception {
    assertThat("Taget must be non-existant", target.exists(), is(FALSE));

    final File moduleXml = new File(project.getBasedir(), source);
    moduleXml.mkdirs();

    String templatePath = project.getBasedir().getName() + source;
    doThrow(new RuntimeException("JUnit")).when(velocity).renderModuleXml(any(), eq(templatePath));

    testling.parseModuleVm();
  }

}