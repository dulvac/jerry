## Jerry Server
Jerry is a *very* simple web browser done in Java.
This project is just a sketch and should be treated as such.

## Description
Jerry uses thread pools. No nio here.
It relies heavily on Apache's [httpcomponents](http://hc.apache.org/httpcomponents-core-ga/index.html).
The project started from the ElementalHttp example from the [examples page](http://hc.apache.org/httpcomponents-core-ga/examples.html)
Also, I couldn't help but get inspiration from [Jetty](https://github.com/eclipse/jetty.project)

## Requirements
* Maven 3
* java 5 or higher

# Building & Running
```mvn package``` to... well, package.

Configuration stays outside so it's easy to change.
```bin/jerry_server.sh``` or run the `JerryServer` class with `target/dependency`,
`conf/` and obviously the jar as part of the CLASSPATH if Unix is not your flavour.

## Logging
[slf4j](http://www.slf4j.org/) and [logback](http://logback.qos.ch/) because they're awesome.
I started by using [log4j 2](http://logging.apache.org/log4j/2.x/) just to give it a chance,
but it just got to beta.

## Packaging
[Maven](http://maven.apache.org/) because it works.

## Configuration
Configuration files are found in `conf/`.
* `logback.xml` - logging configuration; look at [the manual](http://logback.qos.ch/manual/) if you need help.
* `jerry.xml` - server configuration; Allows adding multiple listeners on different ports and with different document
roots.

## Enhancements
* Change architecture so it can accommodate something other than `httpcomponents`.
* Use [netty](https://netty.io/) or something and keep a pool of connections.



