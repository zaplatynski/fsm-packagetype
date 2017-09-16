# Maven Package Type For FSMs [![Build Status](https://travis-ci.org/zaplatynski/fsm-packagetype.svg?branch=master)](https://travis-ci.org/zaplatynski/fsm-packagetype) [![Coverage Status](https://coveralls.io/repos/github/zaplatynski/fsm-packagetype/badge.svg)](https://coveralls.io/github/zaplatynski/fsm-packagetype) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.zaplatynski/fsm-packagetype/badge.svg?style=flat)](http://mvnrepository.com/artifact/com.github.zaplatynski/fsm-packagetype)

This is a basic approach to create a Maven package type for FirstSpirit modules (FSM) with a 
fully working Maven lifecycle inclung install and deploy. Additionally there is support to crate 
the FSM deployment descriptor called module.xml. For information please consult the official 
documentations.

## How to use

In your `pom.xml` add this:
```xml
<project>

    <groupId>my-group</groupId>
    <artifactId>my-fsm-artifact</artifactId>
    <version>1.2.3</version>
    
    <!-- NEW: make a FSM file -->
    <packaging>fsm</packaging>

    ...
    <build>
    
        <plugins>
            
            <!-- make new FSM package type available to Maven -->
            <plugin>
                <groupId>com.github.zaplatynski</groupId>
                <artifactId>fsm-packagetype</artifactId>
                <version>2.3.0</version>
                <!-- this is important when extending core Maven functionality: -->
                <extensions>true</extensions>
            </plugin>
            
            <!-- define how the FSM file look like -->
            <plugin>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>3.0.0</version>
                <configuration>
                    <attach>false</attach>
                    <appendAssemblyId>false</appendAssemblyId>
                    <descriptors>
                        <descriptor>src/assembly/fsm.xml</descriptor>
                    </descriptors>
                </configuration>
                <executions>
                    <execution>
                        <id>make-assembly</id>
                        <phase>prepare-package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            
        </plugins>    
        ...
    </build>
</project>
```
To create the `module.xml` (FirstSpirit module deployment descriptor) you must provide a `module
.vm` ([Apache Velocity macro](http://velocity.apache.org/engine/devel/user-guide.html)) in the path `src/main/fsm` with content:
```
#defaultModuleXml($project)
```
The example (see [macros.vm](src/main/resources/macros.vm)) above will add the common tags for 
name, version etc. (sub macro #addHeader) and collect module fragment xml if avaiable
(sub macro #addModuleXmlFragments). Besides those three macros the variable $project give access 
to the whole
[Maven project](https://maven.apache.org/ref/3.2.3/apidocs/org/apache/maven/project/MavenProject.html).
In addition all user defined Maven properties are available too. Since Velocity can not deal with dots in variable names please name them accordingly.

### FirstSpirit Mode Isolated

To enable FirstSpirit upcoming mode insolated inside the module deployment descriptor simply add 
this property to your pom.xml:

```xml
<project>
    ...
    <properties>
        ...
        <fsmode>isolated</fsmode>
        ...
    </properties>
    ...
</project>
```
The default Velocity macros will recognized it and create the the following attribute inside resource 
tags:
```xml
<module>
    ...
    <components>
    ...
    <resources>
        ...
        <resource mode="isolated" ... />
        ...
    </resources>
    ...
    </components>
    ...
</module>
```

### Module Deployment Descriptor Fragment

To create an module fragment xml in any other jar Maven module just this to the `pom.xml`:
```xml
</project>
...
    <build>
        ...
        <plugins>
            ...
            <plugin>
                <groupId>com.github.zaplatynski</groupId>
                <artifactId>fsm-packagetype</artifactId>
                <version>2.3.0</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>fragmentModuleXml</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            ...
        </plugins>
    </build>
...
</project>
```
Again in the `src/main/fsm` there must be an in the path `module-fragment.vm` in which you can 
define e.g. an FirstSpirit Executable or Service.

### FSM Layout

Inside the above mentioned `fsm.xml` you need to specify the [Maven assembly plugin](http://maven.apache.org/plugins/maven-assembly-plugin/) descriptor to create a typical FSM file layout:
```xml
<assembly xmlns="http://maven.apache.org/ASSEMBLY/2.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/ASSEMBLY/2.0.0 http://maven.apache.org/xsd/assembly-2.0.0.xsd">
    <id>fsm</id>
    <formats>
        <format>zip</format>
    </formats>
    <includeBaseDirectory>false</includeBaseDirectory>
    <files>
        <file>
            <source>target/module.xml</source>
            <outputDirectory>META-INF</outputDirectory>
            <filtered>false</filtered>
        </file>
        <file>
            <source>target/${project.artifactId}-${project.version}.jar</source>
            <outputDirectory>lib</outputDirectory>
            <filtered>false</filtered>
        </file>
    </files>
    <dependencySets>
        <dependencySet>
            <outputDirectory>lib</outputDirectory>
            <useTransitiveFiltering>true</useTransitiveFiltering>
            <useProjectArtifact>false</useProjectArtifact>
        </dependencySet>
    </dependencySets>
</assembly>
```
The FSM Maven package type will take care to rename the zip file to a FSM file. In the dependency set you specify yourself.
 
If you want to have a kind of real world example then have a look at my [Second-Hand Log project](https://github.com/zaplatynski/second-hand-log) or my [FSM Libray Creator project](https://github.com/zaplatynski/fsm-library-creator) here on GitHub. 

## Build command

[Maven](http://maven.apache.org/) is used to compile and assemble this project:
```
mvn clean install
```

## Help, bugs and feature requests

Please file any **request for help**, **bug** or **feature request** at [github.com/zaplatynski/fsm-packagetype/issues](https://github.com/zaplatynski/fsm-packagetype/issues).

##  Disclaimer

By using it you agree to the license stated in the file [LICENSE](LICENSE). FirstSpirit is a trade mark by the [e-Spirit AG](http://www.e-spirit.com/).

