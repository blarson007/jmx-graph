# jmx-graph

## Overview
The goal of this project is to provide a tool that can be spun up with minimal effort to monitor the health of a Java application.

## Technologies used
An embedded Tomcat container provides a web console for the end user.  
Quartz is used to schedule the JMX polling of the target application.  
HSQLDB is used as an embedded database to store the JMX results. An in-memory option is available, but the default is file-based.  
Chartist is used with moment.js to display the data in graphical form to the end user.

## Build instructions
Java and Maven are required to build the project. To build, clone the project and enter ```mvn clean package``` from the project's home directory. This will generate a runnable uber jar in the 'target' folder.

## Run instructions
To run the application, navigate to the uber jar and enter ```java -jar jmx-client-[version]-with-dependencies.jar```. Replace [version] with the version of the artifact. Navigate to http://localhost:8081 when the application has started up successfully.
