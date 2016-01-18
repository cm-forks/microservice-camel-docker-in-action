# INSTRUCTIONS

The MicroService Camel REST in action project consists of 2 maven modules `camel-rest-client` and `camel-rest-service`; one containing the code to play the role of a client sending HTTP requests and calling a REST Service exposed by another
project. They both will be created as Microservice as they will be able to run into their own JVM, Container, without any ESB Bus, will be managed separately and independently.

# Project creation

## iPaas Archetype

* Use this Camel Archetype to create the skeleton of the project which is a camel-cdi maven module

```
mvn archetype:generate

19: remote -> io.fabric8.archetypes:cdi-camel-http-archetype (Creates a new Camel route using CDI in a standalone Java Container calling the remote camel-servlet quickstart))
```

with these parameters

```
Detail to be used to set the maven archetype

```
Project : camel-cdi-rest
Package : org.jboss.fuse
Version: 1.0-SNAPSHOT
```

This archetype will be used as input to create a Camel route using the CDI Framework to inject the Beans and start the CamelContext. The purpose
of the Apache Camel Route will be to send every 5s a message a HTTP request to the REST service using a Netty4-HTTP Endpoint as described hereafter.

```
@ContextName("myCdiCamelContext")
public class MyRoutes extends RouteBuilder {

    @Inject
    @Uri("timer:foo?period=5000")
    private Endpoint inputEndpoint;

    @Inject
    /** Local **/
    //@Uri("netty4-http:http://localhost:8080?keepalive=false&disconnect=true")

    /** Docker Container **/
    // @Uri("netty4-http:http://172.17.0.8:8080?keepalive=false&disconnect=true")

    /** Pod Container + Kubernetes Service  **/
    @Uri("netty4-http:http://{{service:hellorest}}?keepalive=false&disconnect=true")
    private Endpoint httpEndpoint;

    @Inject
    private SomeBean someBean;

    @Override
    public void configure() throws Exception {
        // you can configure the route rule with Java DSL here

        from(inputEndpoint)
            .setHeader("user").method(someBean,"getRandomUser")
            .setHeader("CamelHttpPath").simple("/camel/users/${header.user}/hello")
            .to(httpEndpoint)
            .log("Response : ${body}");
```

The url of the endpoint will be changed according to the environment where we will run the route: local, docker daemon or openshift v3.

The method `getRandomUser` has been added within the someBean class to generate from a list, the user saying Hello

```
@Singleton
@Named("someBean")
public class SomeBean {

    static List<String> sales;

    public SomeBean() {
        sales = new ArrayList<String>();
        sales.add("James Strachan");
        sales.add("Claus Ibsen");
        sales.add("Hiram Chirino");
        sales.add("Jeff Bride");
        sales.add("Chad Darby");
        sales.add("Rachel Cassidy");
        sales.add("Bernard Tison");
        sales.add("Nandan Joshi");
        sales.add("Rob Davies");
        sales.add("Guillaume Nodet");
        sales.add("Marc Little");
        sales.add("Mario Fusco");
        sales.add("James Hetfield");
        sales.add("Kirk Hammett");
        sales.add("Steve Perry");
    }

    private int counter;

    public static String getRandomUser() {
        //0-11
        int index = new Random().nextInt(sales.size());
        return sales.get(index);
    }
```

The detail to be used to set the maven archetype is defined hereafter:

```
Project : camel-cdi-rest
Package : org.jboss.fuse
Version: 1.0-SNAPSHOT
```

The next project will be designed using the camel web archetype which is a Servlet Tomcat application and will be used to expose using the Camel REST DSL
a REST service to get a User Hello Message.

```
mvn archetype:generate
51: remote -> io.fabric8.archetypes:war-camel-servlet-archetype (Creates a new Camel route using Servlet deployed as WAR)
```

The REST GET Service is defined as such : `/camel/users/${id_of_the_user}/hello` and this message wil lbe reurned '"Hello " + id + "! Welcome from pod/docker host : " + System.getenv("HOSTNAME")'

The detail to be used to set the maven archetype is defined hereafter:

```
Project : camel-rest
Package : org.jboss.fuse
Version: 1.0-SNAPSHOT
```

Remarks : We will use the following images (s2i-java for the camel cdi project and tomcat-8.0 for the camel web project) if this is not yet the case

```
      <docker.from>fabric8/s2i-java:1.2</docker.from>
      <docker.from>fabric8/tomcat-8.0</docker.from>
```

## Using JBoss Forge

* Instead of iPaas Archetypes, we will use the standard Camel Archetypes to create the skeleton of the project and next we will run the JBoss Forge command to setup the project

```
mvn archetype:generate
58: remote -> org.apache.camel.archetypes:camel-archetype-cdi (Creates a new Camel project using CDI.)

with these parameters

Project : camel-cdi-rest
Package : org.jboss.fuse
Version: 1.0-SNAPSHOT
```
and

```
mvn archetype:generate
19: remote -> io.fabric8.archetypes:cdi-camel-http-archetype (Creates a new Camel route using CDI in a standalone Java Container calling the remote camel-servlet quickstart))

with these parameters

Project : camel-cdi-rest
Package : org.jboss.fuse
Version: 1.0-SNAPSHOT

```
* Next, we will run the `fabric8-setup` forge commands within each maven module created. This command will to add the `Docker/Fabric8 maven plugins` and will update the maven properties with
  the information required by the maven plugins.

* Due to some issues discovered with the latest JBoss Forge fabric8-setup, some adjustments have been required as described here after
* Move `<name>` and `<from>` tags within the docker configuration of the Docker maven plugin and change to the version of the Docker maven plugin to 0.13.6

```
<plugin>
  <groupId>org.jolokia</groupId>
  <artifactId>docker-maven-plugin</artifactId>
  <version>${docker.maven.plugin.version}</version>
  <configuration>
    <images>
      <image>
        <name>${docker.image}</name>
        <build>
          <from>${docker.from}</from>
```
* We will use the following images instead of the images added by the fabric8-setup forge command `fabric8/s2i-java` and `fabric8/tomcat-8`

```
      <docker.from>fabric8/s2i-java:1.2</docker.from>
      <docker.from>fabric8/tomcat-8.0</docker.from>
```

# Use Docker daemon started with boot2docker or docker-machine

* Launch docker-machine in a terminal and start the default virtual machine using this command `docker-machine start default`

* Within a terminal whre your development project has been created, set the ENV variables required to access and communicate with the
  Docker daemon

```
    eval $(docker-machine env default)
    export DOCKER_TLS_VERIFY="1"
    export DOCKER_HOST="tcp://192.168.99.100:2376"
    export DOCKER_CERT_PATH="/Users/chmoulli/.docker/machine/machines/default"
    export DOCKER_MACHINE_NAME="default"
    export DOCKER_REGISTRY="192.168.99.100:5000"
```

* Add the DOCKER_IP env variable

```
    export DOCKER_IP=192.168.99.100
```

* Redirect the traffic from the Host to the Docker Virtual Machine

```
    sudo route -n delete 172.0.0.0/8
    sudo route -n add 172.0.0.0/8 $DOCKER_IP
```

4) Install a docker registry when using Docker with boot2docker or docker-machine as we have to push our build (= docker tar files) to a local registry

    docker run -d -p 5000:5000 --restart=always --name registry registry:2

5) Build and push the image of the Camel Rest Example

    docker run -it -p 8080:8080 -p 8778:8778 --name camel-rest-microservice 192.168.99.100:5000/fabric8/rest:1.0-SNAPSHOT

6) Find IP address of the docker container

    docker ps --filter="name=rest" | awk '{print $1}' | xargs docker inspect | grep "IPAddress"

7)  Change the url of the camel CDI Route to point to this Hostname

    @Uri("netty4-http:http://DOCKER_CONTAINER_IPADDRESS:8080/camel/hello?keepalive=false&disconnect=true")

9) Build the Image of the Camel CDI and create the docker container

    mvn clean install docker:build

    docker run -it --name camel-cdi-microservice 192.168.99.100:5000/fabric8/cdi:1.0-SNAPSHOT

10) Connect to the Tomcat console and add the hawtio war

    http://172.17.0.7:8080/manager/html

    The IP address depends on the address generated by the docker container
    User / password : admin/admin

    Install the hawtio-default.war file available here : http://repo1.maven.org/maven2/io/hawt/hawtio-default/1.4.59/hawtio-default-1.4.59.war

11) You can access now to your Camel routes

    http://172.17.0.7:8080/hawtio-default-1.4.59/welcome

12) Use the Camel CDI Rest client

    Please change the IP adress of the Netty4 HTTP url within the Camel Route
    docker run -it --name camel-cdi-rest-microservice 192.168.99.100:5000/fabric8/cdi-rest:1.0-SNAPSHOT

13) Add fabric8 properties to generate the Kubernetes json file containing the service to be exposed

    @Uri("netty4-http:http://{{service:hellorest}}?keepalive=false&disconnect=true")
    private Endpoint httpEndpoint;

# Use openshift v3

* Use Fabric8-installer to seyup locally openshift v3 & docker daemon

15) Create a demo namespace/project

    oc login https://172.28.128.4:8443
    oc new-project demo

16) Build and deploy the pod of Camel REST Service (Tomcat)

    export KUBERNETES_DOMAIN=vagrant.f8
    export DOCKER_HOST=tcp://vagrant.f8:2375
    mvn -Pf8-build
    mvn -Pf8-local-deploy

17) Build and deploy the pod of Camel REST Client

    export KUBERNETES_DOMAIN=vagrant.f8
    export DOCKER_HOST=tcp://vagrant.f8:2375
    mvn -Pf8-build
    mvn -Pf8-local-deploy

18) Verify in openshift / Fabric console

19) Increase the controller of the REST service to support loadbalancing

