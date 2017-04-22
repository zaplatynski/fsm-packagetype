package com.github.zaplatynski.firstspirit.modules.fsm.velocity;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class VelocityManagerTest {

  @Rule
  public MockitoRule injectMocks = MockitoJUnit.rule();

  @Rule
  public TemporaryFolder temporary = new TemporaryFolder();

  @Mock
  private MavenProject project;

  @Mock
  private MavenProject parentProject;

  @Mock
  private ArtifactHandler artifactResolver;

  private VelocityManager testling;
  private File sourceFile;
  private File templateRoot;
  private Properties mavenProjectProperties;
  private String relativeModuleVMPath;

  @Before
  public void setUp() throws Exception {

    mavenProjectProperties = new Properties();
    mavenProjectProperties.setProperty("customVariable","This is a custom value.");
    when(project.getProperties()).thenReturn(mavenProjectProperties);

    templateRoot = temporary.getRoot();
    assertThat(templateRoot.exists(), is(true));
    when(parentProject.getBasedir()).thenReturn(templateRoot);
    when(project.getParent()).thenReturn(parentProject);

    final File parent = new File(templateRoot, "project/src/main/fsm/");
    parent.mkdirs();
    sourceFile = new File(parent,"module.vm");
    sourceFile.createNewFile();

    assertThat(sourceFile.exists(), is(true));
    relativeModuleVMPath = "project/src/main/fsm/module.vm";

    testling = new VelocityManager(project);
  }

  @Test
  public void renderModuleXmlSimpleHeader() throws Exception {
    when(project.getGroupId()).thenReturn("group");
    when(project.getArtifactId()).thenReturn("test");
    when(project.getVersion()).thenReturn("1.0");

    RandomAccessFile moduleVM = new RandomAccessFile(sourceFile, "rw");

    moduleVM.writeBytes("<module>\n#addHeader($project)\n</module>");
    moduleVM.close();

    Writer writer = new StringWriter();

    testling.renderModuleXml(writer, relativeModuleVMPath);

    assertThat(writer.toString(), is(
        "<module>\n" +
        "    <name>test</name>\n" +
        "    <version>1.0</version>\n" +
        "</module>"));
  }

  @Test
  public void renderModuleXmlWithoutParentProject() throws Exception {

    reset(project);

    when(project.getProperties()).thenReturn(mavenProjectProperties);
    when(project.getParent()).thenReturn(null);
    when(project.getBasedir()).thenReturn(new File(templateRoot, "project"));

    testling = new VelocityManager(project);

    when(project.getGroupId()).thenReturn("group");
    when(project.getArtifactId()).thenReturn("test");
    when(project.getVersion()).thenReturn("1.0");

    RandomAccessFile moduleVM = new RandomAccessFile(sourceFile, "rw");

    moduleVM.writeBytes("<module>\n#addHeader($project)\n</module>");
    moduleVM.close();

    Writer writer = new StringWriter();

    testling.renderModuleXml(writer, relativeModuleVMPath);

    assertThat(writer.toString(), is(
        "<module>\n" +
            "    <name>test</name>\n" +
            "    <version>1.0</version>\n" +
            "</module>"));
  }

  @Test
  public void renderModuleXmlFullHeader() throws Exception {
    when(project.getGroupId()).thenReturn("group");
    when(project.getArtifactId()).thenReturn("test");
    when(project.getVersion()).thenReturn("1.0");
    when(project.getName()).thenReturn("My FSM");
    when(project.getDescription()).thenReturn("My test FSM");

    RandomAccessFile moduleVM = new RandomAccessFile(sourceFile, "rw");

    moduleVM.writeBytes("<module>\n#addHeader($project)\n</module>");
    moduleVM.close();

    Writer writer = new StringWriter();

    testling.renderModuleXml(writer, relativeModuleVMPath);

    assertThat(writer.toString(), is(
        "<module>\n" +
        "    <name>test</name>\n" +
        "    <displayname>My FSM</displayname>\n" +
        "    <description>My test FSM</description>\n" +
        "    <version>1.0</version>\n" +
        "</module>"));
  }

  @Test
  public void renderModuleXmlMavenProperties() throws Exception {
    when(project.getGroupId()).thenReturn("group");
    when(project.getArtifactId()).thenReturn("test");
    when(project.getVersion()).thenReturn("1.0");

    RandomAccessFile moduleVM = new RandomAccessFile(sourceFile, "rw");

    moduleVM.writeBytes("<module>\n#addHeader($project)\n\n$customVariable\n\n</module>");
    moduleVM.close();

    Writer writer = new StringWriter();

    testling.renderModuleXml(writer, relativeModuleVMPath);

    assertThat(writer.toString(), is(
        "<module>\n" +
            "    <name>test</name>\n" +
            "    <version>1.0</version>\n" +
            "\n" +
            "This is a custom value.\n" +
            "\n" +
            "</module>"));
  }

  @Test
  public void renderModuleXmlWitFragments() throws Exception {
    when(project.getGroupId()).thenReturn("group");
    when(project.getArtifactId()).thenReturn("test");
    when(project.getVersion()).thenReturn("1.0");
    when(project.getName()).thenReturn("My FSM");
    when(project.getDescription()).thenReturn("My test FSM");
    when(parentProject.getModules()).thenReturn(Collections.<String>singletonList("jarmodule"));

    RandomAccessFile moduleVM = new RandomAccessFile(sourceFile, "rw");

    moduleVM.writeBytes("<module>\n#addHeader($project)\n\n <components>\n" +
        "#addModuleXmlFragments($project)\n </components>\n\n</module>");
    moduleVM.close();

    final File parent = new File(templateRoot, "jarmodule/target");
    parent.mkdirs();
    File fragmentModuleXml = new File(parent, "module-fragment.xml");
    fragmentModuleXml.createNewFile();

    RandomAccessFile moduleXml = new RandomAccessFile(fragmentModuleXml, "rw");

    moduleXml.writeBytes("<public>\n      <name>My Executable</name>\n      " +
        "<class>MyExecutable</class>\n    </public>\n");
    moduleXml.close();

    Writer writer = new StringWriter();

    testling.renderModuleXml(writer, relativeModuleVMPath);

    assertThat(writer.toString(), is(
        "<module>\n" +
            "    <name>test</name>\n" +
            "    <displayname>My FSM</displayname>\n" +
            "    <description>My test FSM</description>\n" +
            "    <version>1.0</version>\n" +
            "\n" +
            " <components>\n" +
            "    <public>\n" +
            "      <name>My Executable</name>\n" +
            "      <class>MyExecutable</class>\n" +
            "    </public>\n" +
            " </components>\n" +
            "\n" +
            "</module>"));
  }

  @Test
  public void renderModuleXmlWithMavenDependencies() throws Exception {
    final DefaultArtifact artifactCompileScope = new DefaultArtifact("groupId1", "artifdactId1",
        "1.0", "compile", "jar", null,        artifactResolver);
    artifactCompileScope.setFile(new File("artifact1-1.0.jar"));
    final DefaultArtifact artifactRunetimeScope = new DefaultArtifact("groupId2", "artifdactId2",
        "2.0", "runtime", "jar", null,        artifactResolver);
    artifactRunetimeScope.setFile(new File("artifact2-2.0.jar"));

    Set<Artifact> mavenDependencies = new HashSet<>();
    mavenDependencies.add(artifactCompileScope);
    mavenDependencies.add(artifactRunetimeScope);
    mavenDependencies.add(new DefaultArtifact("groupId3","artifdactId3","3.0","test","jar",null,
        artifactResolver));

    when(project.getGroupId()).thenReturn("group");
    when(project.getArtifactId()).thenReturn("test");
    when(project.getVersion()).thenReturn("1.0");
    when(project.getArtifacts()).thenReturn(mavenDependencies);

    RandomAccessFile moduleVM = new RandomAccessFile(sourceFile, "rw");

    moduleVM.writeBytes("<module>\n#addHeader($project)\n\n <resources>\n" +
        "#addResources($project \"module\" \"lib\")\n </resources>\n\n</module>");
    moduleVM.close();

    Writer writer = new StringWriter();

    testling.renderModuleXml(writer, relativeModuleVMPath);

    assertThat(writer.toString(), is(
        "<module>\n" +
            "    <name>test</name>\n" +
            "    <version>1.0</version>\n" +
            "\n" +
            " <resources>\n" +
            "    <resource scope=\"module\" name=\"groupId1:artifdactId1\" version=\"1.0\">" +
            "lib/artifact1-1.0.jar</resource>\n" +
            "    <resource scope=\"module\" name=\"groupId2:artifdactId2\" version=\"2.0\">" +
            "lib/artifact2-2.0.jar</resource>\n" +
            " </resources>\n" +
            "\n" +
            "</module>"));
  }

  @Test
  public void renderModuleXmlWithMavenDependenciesModeIsolated() throws Exception {

    reset(project);

    mavenProjectProperties.setProperty("fsmode", "isolated");
    when(project.getProperties()).thenReturn(mavenProjectProperties);

    when(parentProject.getBasedir()).thenReturn(templateRoot);
    when(project.getParent()).thenReturn(parentProject);

    testling = new VelocityManager(project);

    final DefaultArtifact artifactCompileScope = new DefaultArtifact("groupId1", "artifdactId1",
        "1.0", "compile", "jar", null,        artifactResolver);
    artifactCompileScope.setFile(new File("artifact1-1.0.jar"));
    final DefaultArtifact artifactRunetimeScope = new DefaultArtifact("groupId2", "artifdactId2",
        "2.0", "runtime", "jar", null,        artifactResolver);
    artifactRunetimeScope.setFile(new File("artifact2-2.0.jar"));

    Set<Artifact> mavenDependencies = new HashSet<>();
    mavenDependencies.add(artifactCompileScope);
    mavenDependencies.add(artifactRunetimeScope);
    mavenDependencies.add(new DefaultArtifact("groupId3","artifdactId3","3.0","test","jar",null,
        artifactResolver));

    when(project.getGroupId()).thenReturn("group");
    when(project.getArtifactId()).thenReturn("test");
    when(project.getVersion()).thenReturn("1.0");
    when(project.getArtifacts()).thenReturn(mavenDependencies);

    RandomAccessFile moduleVM = new RandomAccessFile(sourceFile, "rw");

    moduleVM.writeBytes("<module>\n#addHeader($project)\n\n <resources>\n" +
        "#addResources($project \"module\" \"lib\")\n </resources>\n\n</module>");
    moduleVM.close();

    Writer writer = new StringWriter();

    testling.renderModuleXml(writer, relativeModuleVMPath);

    assertThat(writer.toString(), is(
        "<module>\n" +
            "    <name>test</name>\n" +
            "    <version>1.0</version>\n" +
            "\n" +
            " <resources>\n" +
            "    <resource mode=\"isolated\" scope=\"module\" name=\"groupId1:artifdactId1\" " +
            "version=\"1.0\">lib/artifact1-1.0.jar</resource>\n" +
            "    <resource mode=\"isolated\" scope=\"module\" name=\"groupId2:artifdactId2\" " +
            "version=\"2.0\">lib/artifact2-2.0.jar</resource>\n" +
            " </resources>\n" +
            "\n" +
            "</module>"));
  }

}