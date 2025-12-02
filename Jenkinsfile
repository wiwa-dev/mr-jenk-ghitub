pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven6'
    } 

    
    environment {
        DOCKER_HUB_CREDENTIALS = credentials('docker-hub-credentials')
        DOCKER_IMAGE_TAG = "latest"
        COMPOSE_FILE = "docker-compose.yml"
    }
    stages {
        stage('Initialize') {
            steps {
                sh 'java -version'
                sh 'mvn -version'
                sh 'docker --version'
                sh 'docker-compose --version'
            }
        }
        stage('Detect Changed Services') {
            steps {
                script {
                    // Lister les fichiers modifiés au dernier commits
                    def files = sh(script: 'git diff --name-only HEAD^ HEAD', returnStdout: true).trim().split('\n')
                    echo "📄 Fichiers modifiés : ${files}"
                    CHANGED_SERVICES = []
                    CHANGED_SERVER_CONFIG = []
                    FRONTEND_CHANGED = false
                    files.each {
                        file -> if (file.contains('backend/services/config-server')) {
                            CHANGED_SERVER_CONFIG.add("config-server")
                        }
                        if (file.contains('backend/services/discovery')) {
                            CHANGED_SERVER_CONFIG.add("discovery")
                        }
                        if (file.contains('backend/services/gateway')) {
                            CHANGED_SERVER_CONFIG.add("gateway")
                        }
                        if (file.contains('backend/services/user')) {
                            CHANGED_SERVICES.add("user")
                        }
                        if (file.contains('backend/services/product')) {
                            CHANGED_SERVICES.add("product")
                        }
                        if (file.contains('backend/services/media')) {
                            CHANGED_SERVICES.add("media")
                        }
                        if (file.contains('frontend')) {
                            FRONTEND_CHANGED = true
                        }

                        // Si un fichier YAML de config change → ajouter le service correspondant
                        if (file.contains("user-service.yml")) {
                            CHANGED_SERVICES.add("user")
                        }
                        if (file.contains("product-service.yml")) {
                            CHANGED_SERVICES.add("product")
                        }
                        if (file.contains("media-service.yml")) {
                            CHANGED_SERVICES.add("media")
                        }
                    }
                    CHANGED_SERVICES = CHANGED_SERVICES.unique()
                    echo "🔍 Services impactés : ${CHANGED_SERVICES}"
                    echo "🔍 Server Config impactés : ${CHANGED_SERVER_CONFIG}"
                }
            }
        }
        stage('Unit Tests Backend Services') {
            when {
                expression {
                    CHANGED_SERVICES.size() > 0
                }
            }
            steps {
                script {
                    parallel CHANGED_SERVICES.collectEntries {
                        svc -> ["test-${svc}": {
                            dir("backend/services/${svc}") {
                                sh "mvn clean test"
                            }
                        }]
                    }
                }
            }
        }
        // stage('Unit Tess Frontend Services') {
        //     when {
        //         expression {
        //             FRONTEND_CHANGED
        //         }
        //     }
        //     steps {
        //         script {
        //             // TODO
        //         }
        //     }
        // }
        stage('Build Backend Server Config') {
            when {
                expression {
                    CHANGED_SERVER_CONFIG.size() > 0
                }
            }
            steps {
                script {
                    parallel CHANGED_SERVER_CONFIG.collectEntries {
                        svc -> ["build-${svc}": {
                            dir("backend/services/${svc}") {
                                sh "mvn clean package -DskipTests"
                            }
                        }]
                    }
                }
            }
        }
        stage('Build Backend') {
            when {
                expression {
                    CHANGED_SERVICES.size() > 0
                }
            }
            steps {
                script {
                    parallel CHANGED_SERVICES.collectEntries {
                        svc -> ["build-${svc}": {
                            dir("backend/services/${svc}") {
                                sh "mvn clean package -DskipTests"
                            }
                        }]
                    }
                }
            }
        }
        stage('Build Frontend') {
            when {
                expression {
                    FRONTEND_CHANGED
                }
            }
            steps {
                dir("frontend") {
                    sh 'npm ci'
                    sh 'npx ng build --configuration production'
                }
            }
        }

        // Docker Login
        stage('Docker Login') {
            steps {
                sh 'echo $DOCKER_HUB_CREDENTIALS_PSW | docker login -u $DOCKER_HUB_CREDENTIALS_USR --password-stdin'
            }
        }

        // Build Docker Images for Backend Server Config
        stage('Build Backend Server Config Docker Images') {
             when {
                expression {
                    CHANGED_SERVER_CONFIG.size() > 0
                }
            }
            steps {
                script {
                    parallel CHANGED_SERVER_CONFIG.collectEntries {
                        svc -> ["build-docker-${svc}": {
                            dir("backend/services/${svc}") {
                                sh "docker build -t wiwadev01/${svc}:${DOCKER_IMAGE_TAG} ."
                                sh "docker push wiwadev01/${svc}:${DOCKER_IMAGE_TAG}"
                            }
                        }]
                    }
                }
            }
        }
        // Build Docker Images for Backend Services
        stage('Build Backend Services Docker Images') {
             when {
                expression {
                    CHANGED_SERVICES.size() > 0
                }
            }
            steps {
                script {
                    parallel CHANGED_SERVICES.collectEntries {
                        svc -> ["build-docker-${svc}": {
                            dir("backend/services/${svc}") {
                                sh "docker build -t wiwadev01/${svc}-service:${DOCKER_IMAGE_TAG} ."
                                sh "docker push wiwadev01/${svc}-service:${DOCKER_IMAGE_TAG}"
                            }
                        }]
                    }
                }
            }
        }
        // Build Docker Images for Frontend
        stage('Build Frontend Docker Images') {
             when {
                expression {
                    FRONTEND_CHANGED
                }
            }
            steps {
                dir("frontend") {
                    sh "docker build -t wiwadev01/front-service:${DOCKER_IMAGE_TAG} ."
                    sh "docker push wiwadev01/front-service:${DOCKER_IMAGE_TAG}"
                }
            }
        }
        // Deploy Docker Compose
        stage('Deploy') {
            steps {
                sh "docker-compose -f ${COMPOSE_FILE} pull"
                echo '=== 🚀 Démarrage automatique des services Docker ==='
                // # 2️⃣ Démarrer uniquement le Config Server
                echo '[1/3] Lancement du Config Server...'
                sh 'docker-compose -f docker-compose.yml up -d config-server'
                // Attendre 15 secondes
                echo 'Attente de 10 secondes que le Config Server soit prêt...'
                sh 'sleep 10'
                // 2️⃣ Démarrer uniquement le discovery
                echo '[2/3] Lancement du discovery...'
                sh 'docker-compose -f docker-compose.yml up -d discovery'
                // Attendre 15 secondes
                echo 'Attente de 10 secondes que le discovery soit prêt...'
                sh 'sleep 10'
                // 3️⃣ Démarrer le reste des microservices
                echo '[3/3] Lancement du reste des microservices...'
                sh 'docker-compose -f docker-compose.yml up -d'
                echo '=== ✔ Tous les services sont démarrés ! ==='
            }
        }
    } // <-- fin des stages
    post {
        always {
            echo 'Cleaning up...'
            sh 'docker logout'
        }
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
} // <-- fin du pipeline
