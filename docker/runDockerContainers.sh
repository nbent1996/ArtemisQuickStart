#!/bin/sh
echo Ejecutar contenedores Wildfly
docker run --name CFEXTn1 --network testNetwork --ip 192.168.10.2 -v c:/docker/standaloneCFEXTn1/deployments:/opt/jboss/wildfly/standalone/deployments -v c:/docker/standaloneCFEXTn1/configuration:/opt/jboss/wildfly/standalone/configuration -v c:/docker/scriptsServicioWildfly/levantarCFEXTn1.sh:/opt/jboss/scripts/levantarWildfly.sh -v c:/docker/cacerts:/opt/java/openjdk/lib/security/cacerts --publish 8087:8087 --publish 9997:9997 -d alpine3.14wildfly26.1.1jdk17:1.0

docker run --name CFCOREn1 --log-driver json-file --log-opt max-size=100m --network testNetwork --ip 192.168.10.4 -v c:/docker/standaloneCFCOREn1/deployments:/opt/jboss/wildfly/standalone/deployments -v c:/docker/standaloneCFCOREn1/configuration:/opt/jboss/wildfly/standalone/configuration -v c:/docker/scriptsServicioWildfly/levantarCFCOREn1.sh:/opt/jboss/scripts/levantarWildfly.sh -v c:/docker/cacerts:/opt/java/openjdk/lib/security/cacerts --publish 8287:8287 --publish 10197:10197 -d alpine3.14wildfly26.1.1jdk17:1.0

echo Ejecutar contenedor con broker de mensajeria ActiveMQ Artemis
docker run  -d -e AMQ_USER=admin -e AMQ_PASSWORD=admin1234 --network testNetwork --ip 192.168.10.8 --publish 8161:8161 --publish 61616:61616 --publish 5445:5445 -v c:/docker/CFBROKERn1/broker:/home/jboss/broker --name CFBROKERn1 activemq-artemis-broker2.26:1.0

echo Ejecutar proxy nginx configurado para fixear problema de que el back office no muestra nada.
docker run --rm -d --name CFproxyArtemis --network testNetwork --ip 192.168.10.9 -v c:/docker/nginxProxyArtemis/default.conf:/etc/nginx/conf.d/default.conf -p 8162:80 nginx
