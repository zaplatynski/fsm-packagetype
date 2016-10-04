# Maven Package Type For FSMs [![Build Status](https://travis-ci.org/zaplatynski/fsm-packagetype.svg?branch=master)](https://travis-ci.org/zaplatynski/fsm-packagetype)  [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.zaplatynski/fsm-packagetype/badge.svg?style=flat)](http://mvnrepository.com/artifact/com.github.zaplatynski/fsm-packagetype)

This is a simple approach to create a Maven package type for FirstSpirit modules (FSM) with a fully working Maven lifecycle inclung install and deploy.

## How to use

In your `pom.xml` add this:
```
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
                <version>2.0</version>
                <!-- this is important when extending core Maven functionality: -->
                <extensions>true</extensions>
            </plugin>
            
            <!-- define how the FSM file look like -->
             <plugin>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>2.6</version>
                <configuration>
                    <attach>false</attach>
                    <appendAssemblyId>false</appendAssemblyId>
                    <descriptors>
                        <descriptor>src/assembly/fsm.xml</descriptor>
                    </descriptors>
                </configuration>
            </plugin>
            
        </plugins>    
        ...
    </build>
</project>
```
To create the `module.xml` (FirstSpirit module descriptor) you must provide a `module.vm` ([Apache Velocity macro](http://velocity.apache.org/engine/devel/user-guide.html)) in the path `src/main/fsm`:
```
<module>
    #addHeader($project)
    <components>
        #addModuleXmlFragments($project)
    </components>

    <resources>
        #addResources($project "module" "/lib")
    </resources>
</module>
```
The example above will add the common tags for name, version etc., collect module fragment xml if avaiable and prints at the end all Maven dependencies as resource tags.

To create an module fragment xml in any other jar Maven module just this to the `pom.xml`:
```
</project>
....
    <build>
        ...
        <plugins>
            ...
            <plugin>
                <groupId>com.github.zaplatynski</groupId>
                <artifactId>fsm-packagetype</artifactId>
                <version>2.0</version>
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
Again in the path `src/main/fsm` there must be an in the path `module.vm` in which you can define e.g. an FirstSpirit Executable or Service.

Inside the `fsm.xml` you need to specify the [Maven assembly plugin](http://maven.apache.org/plugins/maven-assembly-plugin/) descriptor to create a typical FSM file layout:
```
<assembly xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xmlns="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.3"
          xsi:schemaLocation="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.3 http://maven.apache.org/xsd/assembly-1.1.3.xsd">
    <id>fsm</id>
    <formats>
        <format>zip</format>
    </formats>
    <includeBaseDirectory>false</includeBaseDirectory>
    <files>
        <file>
            <source>target/module.xml</source>
            <outputDirectory>META-INF</outputDirectory>
            <filtered>true</filtered>
        </file>
    </files>
    <dependencySets>
        <dependencySet>
            <outputDirectory>lib</outputDirectory>
            <includes>
                <include>my-groupId:my-jar-artifactId</include>
                 ...
            </includes>
            <useTransitiveFiltering>true</useTransitiveFiltering>
        </dependencySet>
    </dependencySets>
</assembly>
```
The FSM Maven package type will take care to rename the zip file to a FSM file. In the dependency set you specify your main dependencies.
 
If you want to have a kind of real world example then have a look at my [Second-Hand Log project](https://github.com/zaplatynski/second-hand-log) or my [FSM Libray Creator project](https://github.com/zaplatynski/fsm-library-creator) here on GitHub. As a blue print for more common project setup there is a [example project](https://github.com/zaplatynski/fsm-example-project) here on GitHub too.

## Build command

[Maven](http://maven.apache.org/) is used to compile and assemble this project:
```
mvn clean install
```

##  Disclaimer

By using it you agree to the license stated in the file [LICENSE](LICENSE). FirstSpirit is a trade mark by the [e-Spirit AG](http://www.e-spirit.com/).

