package de.marza.firstspirit.modules.fsm;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RenameAndAttachFSMTest {

  @Rule
  public TemporaryFolder temporary = new TemporaryFolder();

  private RenameZipAndAttachFsm testling;
  private File source;
  private File target;

  @Mock
  private MavenProject project;

  @Mock
  private Artifact artifact;

  @Before
  public void setUp() throws Exception {

    source = temporary.newFile("my.zip");
    target = new File(source.getParent(), "my.fsm");

    testling = new RenameZipAndAttachFsm(project, source, target);

    when(project.getArtifact()).thenReturn(artifact);
  }

  @Test
  public void engage() throws Exception {

    assertThat("Expect existant source", source.exists(), is(true));
    assertThat("Expect non existant target", target.exists(), is(false));

    testling.engage();

    assertThat("Expect non existant source", source.exists(), is(false));
    assertThat("Expect existant target", target.exists(), is(true));

    verify(artifact).setFile(target);
  }

  @Test(expected = MojoFailureException.class)
  public void engageWithDirectorySource() throws Exception {

    source.delete();
    source = temporary.newFolder("my.zip");

    testling = new RenameZipAndAttachFsm(project, source, target);

    assertThat("Expect existant source", source.exists(), is(true));
    assertThat("Expect non existant target", target.exists(), is(false));

    testling.engage();
  }

  @Test
  public void engageWithExistingTarget() throws Exception {

    target = temporary.newFile("my123.fsm");

    testling = new RenameZipAndAttachFsm(project, source, target);

    assertThat("Expect existant source", source.exists(), is(true));
    assertThat("Expect existant target", target.exists(), is(true));

    testling.engage();

    assertThat("Expect non existant source", source.exists(), is(false));
    assertThat("Expect existant target", target.exists(), is(true));
  }

  @Test(expected = MojoFailureException.class)
  public void engageMissingSource() throws Exception {

    source.delete();

    assertThat("Expect non existant source", source.exists(), is(false));
    assertThat("Expect non existant target", target.exists(), is(false));

    testling.engage();
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructorProject() throws Exception {
    new RenameZipAndAttachFsm(null, source, target);
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructorSource() throws Exception {
    new RenameZipAndAttachFsm(project, null, target);
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructorTarget() throws Exception {
    new RenameZipAndAttachFsm(project, source, null);
  }


}