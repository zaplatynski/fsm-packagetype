---
title: Maven Package Type For FirstSpirit Modules
---

# Welcome!

Since Maven supports only standard files types such as JARs (*.jar), WARs (*.war), ... there is no 
built-in support for FSMs (*.fsm) which are used by FirstSpirit as file suffix for archives with 
deployment descriptor.

First, the Maven package type for FSMs wants to fix that by making FSMs first-class citizen by 
extending Maven with an own package type called _fsm_ so that the full Maven life cycle is 
supported (pom.xml):

```xml
<project>
    ...
    <packaging>fsm</packaging>
    ...
    <build>
        ...   
        <plugins>
            
            <plugin>
                <groupId>com.github.zaplatynski</groupId>
                <artifactId>fsm-packagetype</artifactId>
                <version>2.3.0</version>
                <extensions>true</extensions>
            </plugin>
            
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
            ...
        </plugins>    
    </build>
    ...
</project>
```
Second, there is support to use Maven's dependency resolution to create FSM's deployment 
descriptor called _module.xml_ to list all the transitive libraries which are used.

## More information

How to use the Maven package type for FSMs is documented in the GitHub repository at 
[github.com/zaplatynski/fsm-packagetype](https://github.com/zaplatynski/fsm-packagetype).

## Help, bugs and feature requests

Please file any **request for help**, **bug** or **feature request** at [github.com/zaplatynski/fsm-packagetype/issues](https://github.com/zaplatynski/fsm-packagetype/issues).

## Download

The FirstSpirit module package type is available at
[Maven Central](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.github.zaplatynski%22%20AND%20a%3A%22fsm-packagetype%22).


## Disclaimer

FirstSpirit is a trade mark by the [e-Spirit AG](https://www.e-spirit.com).
Maven is a trademark of the [Apache Software Foundation](https://www.apache.org).
