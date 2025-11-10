# Architecture Microservices - Projet E-commerce

## Vue d'ensemble

Cette architecture propose une solution complète de microservices pour une plateforme e-commerce avec Spring Boot et Angular, incluant la gestion des utilisateurs, produits et médias.

## Microservices Principaux

### 1. **User Microservice** (`user-service`)
- **Port**: 8081
- **Base de données**: MongoDB
- **Responsabilités**:
  - Inscription des utilisateurs (clients/vendeurs)
  - Authentification et autorisation
  - Gestion des profils utilisateurs
  - Upload d'avatar pour les vendeurs
  - Génération et validation des JWT tokens

**Endpoints principaux**:
```
POST /api/users/register
POST /api/users/login
GET /api/users/profile
PUT /api/users/profile
POST /api/users/avatar
```

### 2. **Product Microservice** (`product-service`)
- **Port**: 8082
- **Base de données**: MongoDB
- **Responsabilités**:
  - CRUD des produits (vendeurs uniquement)
  - Gestion des catégories
  - Association produits-images
  - Validation des permissions vendeur

**Endpoints principaux**:
```
GET /api/products
POST /api/products
PUT /api/products/{id}
DELETE /api/products/{id}
GET /api/products/seller/{sellerId}
```

### 3. **Media Microservice** (`media-service`)
- **Port**: 8083
- **Base de données**: MongoDB + Stockage fichiers
- **Responsabilités**:
  - Upload de fichiers (limite 2MB)
  - Validation des types de fichiers
  - Gestion des images produits
  - Compression et optimisation d'images

**Endpoints principaux**:
```
POST /api/media/upload
GET /api/media/{id}
DELETE /api/media/{id}
GET /api/media/product/{productId}
```

## Infrastructure et Services Support

### 4. **API Gateway** (`api-gateway`)
- **Port**: 8080
- **Technologie**: Spring Cloud Gateway
- **Responsabilités**:
  - Routage des requêtes
  - Authentification centralisée
  - Rate limiting
  - CORS management
  - Load balancing

### 5. **Service Discovery** (`eureka-server`)
- **Port**: 8761
- **Technologie**: Netflix Eureka
- **Responsabilités**:
  - Découverte automatique des services
  - Health checks
  - Service registry

### 6. **Configuration Service** (`config-server`)
- **Port**: 8888
- **Technologie**: Spring Cloud Config
- **Responsabilités**:
  - Gestion centralisée de la configuration
  - Profiles par environnement
  - Refresh dynamique

### 7. **Message Broker** (`kafka-cluster`)
- **Ports**: 9092, 9093, 9094
- **Technologie**: Apache Kafka
- **Responsabilités**:
  - Communication asynchrone entre services
  - Event sourcing
  - Notifications en temps réel

## Services de Support

### 8. **Notification Service** (`notification-service`)
- **Port**: 8084
- **Responsabilités**:
  - Envoi d'emails
  - Notifications push
  - SMS (optionnel)

### 9. **Audit Service** (`audit-service`)
- **Port**: 8085
- **Responsabilités**:
  - Logging des actions utilisateurs
  - Audit trail
  - Monitoring des performances

## Base de Données

### MongoDB Clusters
- **User Database**: `user_db`
- **Product Database**: `product_db`
- **Media Database**: `media_db`
- **Audit Database**: `audit_db`

### Stockage Fichiers
- **Local Storage** ou **AWS S3** pour les images
- **CDN** pour la distribution des médias

## Sécurité

### Authentification & Autorisation
- **JWT Tokens** avec Spring Security
- **OAuth2** pour l'authentification externe
- **Role-based Access Control** (CLIENT/SELLER)
- **HTTPS** obligatoire avec Let's Encrypt

### Sécurité des Données
- **Chiffrement** des mots de passe avec BCrypt
- **Validation** des inputs côté serveur
- **Rate Limiting** par utilisateur
- **CORS** configuré strictement

## Communication Inter-Services

### Synchronous Communication
- **REST APIs** via API Gateway
- **Load Balancing** avec Ribbon
- **Circuit Breaker** avec Hystrix

### Asynchronous Communication
- **Kafka Topics**:
  - `user-events`
  - `product-events`
  - `media-events`
  - `notification-events`

## Monitoring et Observabilité

### Logging
- **Centralized Logging** avec ELK Stack
- **Structured Logging** avec Logback
- **Correlation IDs** pour tracer les requêtes

### Monitoring
- **Health Checks** avec Spring Actuator
- **Metrics** avec Micrometer + Prometheus
- **Distributed Tracing** avec Zipkin

## Déploiement

### Containerisation
- **Docker** pour chaque microservice
- **Docker Compose** pour l'environnement de développement
- **Kubernetes** pour la production

### CI/CD
- **GitHub Actions** ou **Jenkins**
- **Automated Testing** (Unit + Integration)
- **Blue-Green Deployment**

## Architecture Frontend (Angular)

### Structure
```
src/
├── app/
│   ├── components/
│   │   ├── auth/
│   │   ├── product/
│   │   └── media/
│   ├── services/
│   │   ├── auth.service.ts
│   │   ├── product.service.ts
│   │   └── media.service.ts
│   ├── guards/
│   │   └── auth.guard.ts
│   └── interceptors/
│       └── auth.interceptor.ts
```

### Services Angular
- **AuthService**: Gestion de l'authentification
- **ProductService**: CRUD des produits
- **MediaService**: Upload et gestion des médias
- **NotificationService**: Notifications utilisateur

## Flux de Données Principaux

### 1. Inscription Utilisateur
```mermaid
Angular → API Gateway → User Service → MongoDB
                ↓
         Kafka (user-events) → Notification Service
```

### 2. Création Produit
```
Angular → API Gateway → Product Service → MongoDB
                ↓
         Kafka (product-events) → Audit Service
```

### 3. Upload Média
```
Angular → API Gateway → Media Service → File Storage
                ↓
         Kafka (media-events) → Product Service (update)
```

## Environnements

### Développement
- **Docker Compose** pour tous les services
- **MongoDB** en local
- **Kafka** en local
- **HTTPS** désactivé

### Production
- **Kubernetes** cluster
- **MongoDB Atlas** ou cluster dédié
- **Kafka** cluster haute disponibilité
- **HTTPS** avec Let's Encrypt
- **CDN** pour les médias

## Métriques de Performance

### Objectifs
- **Response Time**: < 200ms pour 95% des requêtes
- **Availability**: 99.9% uptime
- **Throughput**: 1000 requêtes/seconde
- **File Upload**: < 5 secondes pour 2MB

Cette architecture garantit la scalabilité, la maintenabilité et la sécurité nécessaires pour une plateforme e-commerce moderne.







