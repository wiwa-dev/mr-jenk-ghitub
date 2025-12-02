# 🐳 Docker & Docker Compose : Le Guide Ultime CI/CD

Ce document recense plus de 100 commandes et variations essentielles pour la gestion, le débogage et l'automatisation des pipelines CI/CD (Jenkins, GitLab CI, Spring Boot, Angular).

---

## 📋 Table des Matières
1. [Initialisation & Registres](#1-initialisation--registres)
2. [Construction (Build)](#2-construction-build)
3. [Gestion des Images](#3-gestion-des-images)
4. [Exécution des Conteneurs (Run)](#4-exécution-des-conteneurs-run)
5. [Cycle de Vie des Conteneurs](#5-cycle-de-vie-des-conteneurs)
6. [Interaction & Débogage](#6-interaction--débogage)
7. [Nettoyage & Maintenance (Système)](#7-nettoyage--maintenance-système)
8. [Docker Compose (V2)](#8-docker-compose-v2)
9. [Réseaux (Networking)](#9-réseaux-networking)
10. [Volumes & Persistance](#10-volumes--persistance)
11. [Commandes Avancées pour CI/CD](#11-commandes-avancées-pour-cicd)

---

## 1. Initialisation & Registres
*Commandes pour s'authentifier et vérifier l'état du moteur.*

| Commande | Description |
| :--- | :--- |
| `docker version` | Affiche la version détaillée (client/serveur). |
| `docker info` | Affiche les infos système (nb conteneurs, images, RAM). |
| `docker login` | Connexion interactive au Docker Hub. |
| `docker login -u <user> -p <pass>` | Connexion CLI (⚠️ mot de passe visible). |
| `docker login -u <user> --password-stdin` | **CI/CD Best Practice** : Connexion via pipe (`echo $PASS | docker login...`). |
| `docker login <registry.url>` | Connexion registre privé (Nexus, GitLab). |
| `docker logout` | Déconnexion (à faire en fin de pipeline). |
| `docker context ls` | Liste les contextes (local vs remote). |
| `docker context use <ctx>` | Change le contexte actif. |

---

## 2. Construction (Build)
*Transformer les Dockerfiles en Images.*

| Commande | Description |
| :--- | :--- |
| `docker build .` | Construit depuis le dossier courant. |
| `docker build -t app:v1 .` | Construit et taggue l'image. |
| `docker build -f Dockerfile.dev .` | Utilise un fichier spécifique. |
| `docker build --no-cache .` | **CI/CD** : Force la reconstruction totale. |
| `docker build --pull .` | Télécharge la dernière version de l'image de base. |
| `docker build --build-arg ENV=prod .` | Passe une variable au build. |
| `docker build --target build-stage .` | Build multi-stage : arrêt à une étape précise. |
| `docker build -q .` | Mode silencieux (affiche juste l'ID). |
| `docker build --network host .` | Utilise le réseau hôte pendant le build. |
| `docker image build ...` | Alias explicite. |

---

## 3. Gestion des Images
*Manipulation des images stockées.*

| Commande | Description |
| :--- | :--- |
| `docker images` | Liste les images locales. |
| `docker images -a` | Liste toutes les images (inclus intermédiaires). |
| `docker images -q` | Liste uniquement les IDs. |
| `docker pull nginx` | Télécharge une image. |
| `docker pull nginx:alpine` | Télécharge une version précise. |
| `docker push user/app:v1` | Envoie l'image sur le registre. |
| `docker tag source:v1 target:latest` | Crée un alias (nouveau tag). |
| `docker rmi <id>` | Supprime une image. |
| `docker rmi -f <id>` | Force la suppression. |
| `docker rmi $(docker images -q)` | **Danger** : Supprime toutes les images. |
| `docker image prune` | Supprime les images "dangling" (sans tag). |
| `docker image prune -a` | Supprime toutes les images non utilisées. |
| `docker history <image>` | Affiche les couches (layers). |
| `docker save -o backup.tar <img >` | Exporte une image en tar. |
| `docker load -i backup.tar` | Importe une image depuis un tar. |
| `docker inspect <image>` | Affiche le JSON de configuration. |

---

## 4. Exécution des Conteneurs (Run)
*Lancer une instance.*

| Commande | Description |
| :--- | :--- |
| `docker run nginx` | Lance au premier plan. |
| `docker run -d nginx` | **Detached** : Lance en arrière-plan. |
| `docker run --name web nginx` | Nomme le conteneur. |
| `docker run -p 8080:80 nginx` | Mappe le port hôte:conteneur. |
| `docker run -P nginx` | Mappe tous les ports aléatoirement. |
| `docker run -e VAR=1 nginx` | Injecte une variable d'environnement. |
| `docker run --env-file .env nginx` | Injecte un fichier de variables. |
| `docker run -v /h:/c nginx` | Monte un volume (Hôte -> Conteneur). |
| `docker run --rm nginx` | **CI/CD** : Auto-suppression à l'arrêt. |
| `docker run --restart always nginx` | Redémarrage auto si crash. |
| `docker run --net my-net nginx` | Connecte à un réseau. |
| `docker run -it ubuntu bash` | Terminal interactif. |
| `docker run --entrypoint sh ...` | Écrase la commande de démarrage. |
| `docker run -u root ...` | Force l'utilisateur root. |
| `docker run --cpus=".5" nginx` | Limite l'utilisation CPU. |
| `docker run --memory="512m" nginx` | Limite l'utilisation RAM. |

---

## 5. Cycle de Vie des Conteneurs
*Gérer l'état.*

| Commande | Description |
| :--- | :--- |
| `docker ps` | Liste les conteneurs actifs. |
| `docker ps -a` | Liste tout (actifs + arrêtés). |
| `docker ps -q` | Liste seulement les IDs. |
| `docker ps -s` | Affiche la taille disque. |
| `docker start <ctn>` | Démarre un conteneur. |
| `docker stop <ctn>` | Arrête proprement (SIGTERM). |
| `docker stop -t 30 <ctn>` | Attend 30s avant de tuer. |
| `docker restart <ctn>` | Redémarre. |
| `docker kill <ctn>` | Arrêt brutal (SIGKILL). |
| `docker pause <ctn>` | Suspend les processus. |
| `docker unpause <ctn>` | Reprend les processus. |
| `docker rm <ctn>` | Supprime un conteneur arrêté. |
| `docker rm -f <ctn>` | Force la suppression (actif). |
| `docker rm $(docker ps -aq)` | **Cleanup** : Supprime tout ce qui est arrêté. |
| `docker rename old new` | Renomme un conteneur. |

---

## 6. Interaction & Débogage
*Analyser les problèmes.*

| Commande | Description |
| :--- | :--- |
| `docker logs <ctn>` | Affiche les logs. |
| `docker logs -f <ctn>` | Suit les logs en direct. |
| `docker logs --tail 100 <ctn>` | 100 dernières lignes. |
| `docker logs -t <ctn>` | Ajoute le timestamp. |
| `docker exec -it <ctn> bash` | Ouvre un shell DANS le conteneur. |
| `docker exec -it <ctn> sh` | Shell pour Alpine Linux. |
| `docker exec <ctn> ls -la` | Commande unique sans entrer. |
| `docker attach <ctn>` | S'attache au processus principal. |
| `docker cp src dest` | Copie fichier Hôte <-> Conteneur. |
| `docker top <ctn>` | Affiche les processus (PID). |
| `docker stats` | Monitoring CPU/RAM live. |
| `docker stats --no-stream` | Snapshot des stats. |
| `docker diff <ctn>` | Fichiers modifiés depuis création. |
| `docker inspect <ctn>` | Config complète (IP, Vols). |
| `docker port <ctn>` | Affiche les mappings de ports. |

---

## 7. Nettoyage & Maintenance (Système)
*Éviter le "Disk Space Full" sur Jenkins.*

| Commande | Description |
| :--- | :--- |
| `docker system df` | Espace disque utilisé. |
| `docker system prune` | Supprime données inutilisées (soft). |
| `docker system prune -a` | Supprime TOUT (hard). |
| `docker system prune --volumes` | Inclut les volumes (⚠️ Danger). |
| `docker system prune -f` | Force sans confirmation. |
| `docker container prune` | Supprime conteneurs arrêtés. |
| `docker volume prune` | Supprime volumes orphelins. |
| `docker network prune` | Supprime réseaux vides. |
| `docker builder prune` | Vide le cache de build. |

---

## 8. Docker Compose (V2)
*Orchestration locale (app.yml).*

| Commande | Description |
| :--- | :--- |
| `docker compose up` | Démarre tout (logs visibles). |
| `docker compose up -d` | Démarre en fond. |
| `docker compose up --build` | Force le rebuild. |
| `docker compose up --no-deps <svc>` | Démarre un service isolé. |
| `docker compose up --force-recreate` | Force la recréation. |
| `docker compose down` | Arrête et supprime tout. |
| `docker compose down -v` | Supprime aussi les volumes (DB!). |
| `docker compose down --rmi all` | Supprime les images générées. |
| `docker compose stop` | Arrête sans supprimer. |
| `docker compose start` | Démarre les services arrêtés. |
| `docker compose restart` | Redémarre tout. |
| `docker compose logs -f` | Logs agrégés. |
| `docker compose logs -f <svc>` | Logs d'un service. |
| `docker compose ps` | État du stack. |
| `docker compose config` | Valide le fichier YAML. |
| `docker compose exec <svc> bash` | Shell dans un service. |
| `docker compose pull` | Pull des images. |
| `docker compose build` | Build sans lancer. |
| `docker compose top` | Processus par service. |
| `docker compose ls` | Liste les projets Compose actifs. |

---

## 9. Réseaux (Networking)
*Communication inter-services.*

| Commande | Description |
| :--- | :--- |
| `docker network ls` | Liste les réseaux. |
| `docker network create <nom>` | Crée un réseau bridge. |
| `docker network create -d overlay` | Crée un réseau Swarm. |
| `docker network inspect <nom>` | Détails (IPs connectées). |
| `docker network connect <net> <ctn>` | Connecte à chaud. |
| `docker network disconnect` | Déconnecte à chaud. |
| `docker network rm <nom>` | Supprime un réseau. |

---

## 10. Volumes & Persistance
*Données (Bases de données).*

| Commande | Description |
| :--- | :--- |
| `docker volume ls` | Liste les volumes. |
| `docker volume create <nom>` | Crée un volume. |
| `docker volume inspect <nom>` | Chemin physique sur disque. |
| `docker volume rm <nom>` | Supprime un volume. |
| `docker run -v vol:/data` | Utilise un volume nommé. |
| `docker run -v $(pwd):/app` | Bind Mount (dossier actuel). |

---

## 11. Commandes Avancées pour CI/CD
*Scripting Jenkins.*

| Commande | Description |
| :--- | :--- |
| `docker inspect -f '{{.State.Running}}' <id>` | Check si tourne (true/false). |
| `docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' <id>` | Récupère l'IP interne. |
| `docker ps -q -f status=exited` | Trouve les crashs. |
| `docker wait <ctn>` | Attend l'arrêt (retourne exit code). |
| `docker events` | Flux temps réel (monitoring). |
| `docker export <id> > file.tar` | Exporte le filesystem. |
| `docker import file.tar` | Crée image depuis filesystem. |
| `docker commit <ctn> <img:tag>` | Crée image depuis conteneur (déconseillé). |