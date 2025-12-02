#!/bin/bash
set -e  # Stopper le script si une commande échoue

echo "=== 🚀 Démarrage automatique des services Docker ==="

# 1️⃣ Démarrer les services de base (MongoDB, Kafka, etc.)
#echo "[1/3] Lancement des services système (app.yml)..."
#docker compose -f app.yml up -d

# Attendre quelques secondes
#sleep 10

# 2️⃣ Démarrer uniquement le Config Server
echo "[2/3] Lancement du Config Server..."
docker-compose -f docker-compose.yml up -d config-server

# Attendre 10 secondes
echo "Attente de 10 secondes que le Config Server soit prêt..."
sleep 10

# 2️⃣ Démarrer uniquement le discovery
echo "[2/3] Lancement du discovery..."
docker-compose -f docker-compose.yml up -d discovery

# Attendre 15 secondes
echo "Attente de 15 secondes que le discovery soit prêt..."
sleep 15

# 3️⃣ Démarrer le reste des microservices
echo "[3/3] Lancement du reste des microservices..."
docker-compose -f docker-compose.yml up -d

echo "=== ✔ Tous les services sont démarrés ! ==="

