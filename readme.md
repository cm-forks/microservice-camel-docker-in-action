1) Run fabric8-setup forge cmd
2) Move <name> and <from> tags within the docker configuration of the maven plugin and change to the version 0.13.6
3) Set ENV VAR of the docker daemon started locally using `docker-machine start default`

    export DOCKER_TLS_VERIFY="1"
    export DOCKER_HOST="tcp://192.168.99.100:2376"
    export DOCKER_CERT_PATH="/Users/chmoulli/.docker/machine/machines/default"
    export DOCKER_MACHINE_NAME="default"
    export DOCKER_REGISTRY="192.168.99.100:5000"
    export DOCKER_IP=192.168.99.100

    eval $(docker-machine env default)

    sudo route -n delete 172.0.0.0/8
    sudo route -n add 172.0.0.0/8 $DOCKER_IP

4) Install docker registry when using Docker on MAcOS with boot2docker

    docker run -d -p 5000:5000 --restart=always --name registry registry:2

5) Build and push the image of the Camel Web Example

    docker run -it -p 8080:8080 -p 8778:8778 --name camel-web-microservice 192.168.99.100:5000/fabric8/web:1.0-SNAPSHOT

6) Find IP address of the docker container

    docker ps --filter="name=web" | awk '{print $1}' | xargs docker inspect | grep "IPAddress"

7)  Change the url of the camel CDI Route to point to this Hostname

    @Uri("netty4-http:http://172.17.0.5:8080/camel/hello?keepalive=false&disconnect=true")

8) Use the fabric8/S2-i image instead of the fabric8/java image

    <docker.from>fabric8/s2i-java:1.2</docker.from>

9) Build the Image of the Camel CDI and create the docker container

    mvn clean install docker:build

    docker run -it --name camel-cdi-microservice 192.168.99.100:5000/fabric8/cdi:1.0-SNAPSHOT

    OR use the Camel Rest Service

    docker run -it --name camel-rest-microservice -p 8080:8080 -p 8778:8778 192.168.99.100:5000/fabric8/rest:1.0-SNAPSHOT

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

14) Start the openshift / docker server

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

