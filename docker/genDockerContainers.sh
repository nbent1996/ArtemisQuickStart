#!/bin/sh
echo  Generacion de contenedores
docker login

echo Eliminar todas las imagenes custom y contenedores de testingEnvironment para volver a buildear
docker stop CFEXTn1 CFCOREn1 CFBROKERn1 CFproxyArtemis
docker rm CFEXTn1 CFCOREn1 CFBROKERn1 CFproxyArtemis
docker network rm testNetwork
docker rmi alpine3.14wildfly26.1.1jdk17:1.0 activemq-artemis-broker2.26:1.0

#Se hace esto por ser un ambiente de pruebas, no recomendado para entornos de produccion
chmod 777 -R /home/testingEnvironment

echo Generar imagenes con los dockerfile brindados

docker build --no-cache -f dockerfile-WILDFLY . -t alpine3.14wildfly26.1.1jdk17:1.0  
docker pull quay.io/artemiscloud/activemq-artemis-broker:artemis.2.26.0
docker build --no-cache -f dockerfile-ARTEMIS . -t activemq-artemis-broker2.26:1.0

echo Crear subred de contenedores en el rango 192.168.10.0/24
docker network create testNetwork --driver bridge --subnet 192.168.10.0/24