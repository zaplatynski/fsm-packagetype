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
</project>
```
Second, there is support to use Maven's dependency resolution to create FSM's deployment 
descriptor called _module.xml_ to list all the transitive libraries which are used.

## More information

How to use the Maven package type for FSMs is documented in the GitHub repository at 
[github.com/zaplatynski/fsm-packagetype](https://github.com/zaplatynski/fsm-packagetype).

## Disclaimer

FirstSpirit is a trade mark by the [e-Spirit AG](https://www.e-spirit.com).