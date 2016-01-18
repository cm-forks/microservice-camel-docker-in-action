1) Run fabric8-setup forge cmd
2) Move <name> and <from> tags within the docker configuration of the maven plugin and change to the version 0.13.6
3) Set ENV VAR

    export DOCKER_TLS_VERIFY="1"
    export DOCKER_HOST="tcp://192.168.99.100:2376"
    export DOCKER_CERT_PATH="/Users/chmoulli/.docker/machine/machines/default"
    export DOCKER_MACHINE_NAME="default"
    export DOCKER_REGISTRY="192.168.99.100:5000"
    export DOCKER_IP=192.168.99.100

    sudo route -n delete 172.0.0.0/8
    sudo route -n add 172.0.0.0/8 $DOCKER_IP

4) Install docker registry when using Docker on MAcOS with boot2docker

    docker run -d -p 5000:5000 --restart=always --name registry registry:2

5) Build and push the images

    docker run -it -p 8080:8080 -p 8778:8778 --name camel-web-microservice 192.168.99.100:5000/fabric8/web:1.0-SNAPSHOT

6) Find IP address of the docker container

    docker ps --filter="name=web" | awk '{print $1}' | xargs docker inspect | grep "IPAddress"

7)  Change it into the other container (= camel CDI Route)

    @Uri("netty4-http:http://172.17.0.5:8080/camel/hello?keepalive=false&disconnect=true")

8) Build the Image of the Camel CDI and create the docker container

    mvn clean install docker:build

    docker run -it --name camel-cdi-microservice 192.168.99.100:5000/fabric8/cdi:1.0-SNAPSHOT


9) Add fabric8 properties to generate the Kubernetes json file containing the service to be exposed


