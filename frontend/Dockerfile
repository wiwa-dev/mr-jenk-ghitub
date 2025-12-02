# Stage 1: Build
#FROM node:alpine AS build
#WORKDIR /app

# Copier les fichiers de dépendances
#COPY package*.json ./

# Installer les dépendances
#RUN npm ci

# Copier tout le code source
#COPY . .

# Build l'application Angular en mode production
#RUN npm run build -- --configuration production

# Stage 2: Serve avec Nginx
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# Supprimer les fichiers par défaut de nginx
RUN rm -rf ./*

# Copier les fichiers buildés depuis le stage précédent
COPY dist/buy01 .

# Copier la configuration Nginx personnalisée
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exposer le port
EXPOSE 80

# Démarrer nginx
CMD ["nginx", "-g", "daemon off;"]