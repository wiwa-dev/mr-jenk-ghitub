#!/bin/bash

# Variables
JENKINS_CONTAINER_NAME=jenkins
JENKINS_IMAGE=jenkins/jenkins:lts-jdk17
JENKINS_VOLUME=jenkins_home
HOST_DOCKER_SOCK=/var/run/docker.sock
JENKINS_HTTP_PORT=8080
JENKINS_AGENT_PORT=50000
NETWORK_NAME=jenkins-java

echo "=== 🚀 Pull de l'image Jenkins ==="
docker pull $JENKINS_IMAGE

echo "=== 🔥 Création du volume Jenkins (si inexistant) ==="
docker volume inspect $JENKINS_VOLUME >/dev/null 2>&1 || \
    docker volume create $JENKINS_VOLUME

echo "=== 🔗 Création du réseau Docker (si inexistant) ==="
docker network inspect $NETWORK_NAME >/dev/null 2>&1 || \
    docker network create $NETWORK_NAME

# Vérifier si le conteneur existe déjà
if [ $(docker ps -a -q -f name=$JENKINS_CONTAINER_NAME) ]; then
    echo "=== 🛑 Conteneur Jenkins existant détecté, suppression ==="
    docker rm -f $JENKINS_CONTAINER_NAME
fi

# Récupérer le GID du groupe docker sur le host
DOCKER_GID=$(getent group docker | cut -d: -f3)

echo "=== 🚀 Démarrage du conteneur Jenkins avec root et config et Docker CLI et socket ==="
docker run -d \
  --name $JENKINS_CONTAINER_NAME \
  -p $JENKINS_HTTP_PORT:8080 \
  -p $JENKINS_AGENT_PORT:50000 \
  -v $JENKINS_VOLUME:/var/jenkins_home \
  -v $HOST_DOCKER_SOCK:$HOST_DOCKER_SOCK \
  -v $(which docker):/usr/bin/docker \
  -u root \
  -e DOCKER_GID=$DOCKER_GID \
  --network $NETWORK_NAME \
  $JENKINS_IMAGE
sleep 8

echo "=== 🔧 Ajout du groupe docker dans le conteneur ==="
docker exec -u root -it $JENKINS_CONTAINER_NAME groupadd -g $DOCKER_GID docker

echo "=== 🔧 Ajout utilisateur jenkins au groupe docker ==="
docker exec -u root -it $JENKINS_CONTAINER_NAME usermod -aG docker jenkins

echo "=== 🔧 Se deconnecter sur root et aller sur jenkins user ==="
docker exec -u root -it $JENKINS_CONTAINER_NAME su - jenkins && exit

echo "=== ✅ Jenkins démarré ! ==="
echo "Accès : http://<IP_VPS>:$JENKINS_HTTP_PORT"
echo "Pour récupérer le mot de passe admin :"
echo "docker exec -it $JENKINS_CONTAINER_NAME cat /var/jenkins_home/secrets/initialAdminPassword"


