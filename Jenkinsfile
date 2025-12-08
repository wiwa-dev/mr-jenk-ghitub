pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven6'
    }
//
    environment {
        DOCKER_HUB_CREDENTIALS = credentials('docker-hub-credentials')
        DOCKER_IMAGE_TAG_LAST = 'latest'
        DOCKER_IMAGE_TAG_PREV = 'previous'
        COMPOSE_FILE = 'docker-compose.yml'
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
         // Docker Login
        stage('Docker Login') {
            steps {
                sh 'echo $DOCKER_HUB_CREDENTIALS_PSW | docker login -u $DOCKER_HUB_CREDENTIALS_USR --password-stdin'
            }
        }

        stage('Backup Current Version') {
            steps {
                sh 'chmod +x rollback.sh'
                sh './rollback.sh'
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
                            CHANGED_SERVER_CONFIG.add('config-server')
                        }
                        if (file.contains('backend/services/discovery')) {
                            CHANGED_SERVER_CONFIG.add('discovery')
                        }
                        if (file.contains('backend/services/gateway')) {
                            CHANGED_SERVER_CONFIG.add('gateway')
                        }
                        if (file.contains('backend/services/user')) {
                            CHANGED_SERVICES.add('user')
                        }
                        if (file.contains('backend/services/product')) {
                            CHANGED_SERVICES.add('product')
                        }
                        if (file.contains('backend/services/media')) {
                            CHANGED_SERVICES.add('media')
                        }
                        if (file.contains('frontend')) {
                            FRONTEND_CHANGED = true
                        }

                        // Si un fichier YAML de config change → ajouter le service correspondant
                        if (file.contains('user-service.yml')) {
                            CHANGED_SERVICES.add('user')
                        }
                        if (file.contains('product-service.yml')) {
                            CHANGED_SERVICES.add('product')
                        }
                        if (file.contains('media-service.yml')) {
                            CHANGED_SERVICES.add('media')
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
                                sh 'mvn clean test'
                            }
                        }]
                    }
                }
            }
        }
        stage('Unit Tess Frontend Services') {
            when {
                expression {
                    FRONTEND_CHANGED
                }
            }
            steps {
                script {
                    dir('frontend') {
                        sh '''
                        npm ci
                        export CHROME_BIN=/chrome/linux-143.0.7499.40/chrome-linux64/chrome
                        npm run test
                        '''
                    }
                }
            }
        }
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
                                sh 'mvn clean package -DskipTests'
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
                                sh 'mvn clean package -DskipTests'
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
                dir('frontend') {
                    sh '''
                    rm -rf dist
                    npm ci
                    npx ng build --configuration production
                    '''
                }
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
                                sh "docker build -t wiwadev01/${svc}:${DOCKER_IMAGE_TAG_LAST} ."
                                sh "docker push wiwadev01/${svc}:${DOCKER_IMAGE_TAG_LAST}"
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
                                sh "docker build -t wiwadev01/${svc}-service:${DOCKER_IMAGE_TAG_LAST} ."
                                sh "docker push wiwadev01/${svc}-service:${DOCKER_IMAGE_TAG_LAST}"
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
                dir('frontend') {
                    sh "docker build -t wiwadev01/front-service:${DOCKER_IMAGE_TAG_LAST} ."
                    sh "docker push wiwadev01/front-service:${DOCKER_IMAGE_TAG_LAST}"
                }
            }
        }
        // Deploy Docker Compose
        stage('Deploy') {
            steps {
                sh "docker-compose -f ${COMPOSE_FILE} pull"
                sh 'chmod +x start-app.sh'
                sh './start-app.sh'
            }

            post {
                failure {
                    echo '❌ Échec du déploiement → Rollback...'

                    // 1️⃣ Stopper complètement la stack
                    sh "docker-compose -f ${COMPOSE_FILE} down"

                    // 2️⃣ Rollback des images modifiées
                    script {
                        echo '🔄 Rollback des services modifiés...'

                        def services = ['user-service', 'product-service', 'media-service', 'front-service', 'config-server', 'discovery', 'gateway']
                            services.each { svc ->
                                echo "↩️ Rollback du service : ${svc}"

                                // Pull de l'ancienne version...
                                sh """
                        docker pull wiwadev01/${svc}:${DOCKER_IMAGE_TAG_PREV} || true
                        docker tag wiwadev01/${svc}:${DOCKER_IMAGE_TAG_PREV} wiwadev01/${svc}:${DOCKER_IMAGE_TAG_LAST}
                    """
                            }
                    }

                    // 3️⃣ Redémarrer avec les anciennes images ...
                    echo '🚀 Redémarrage avec les images précédentes...'
                    sh 'chmod +x start-app.sh'
                    sh './start-app.sh'

                    echo '✔ Rollback terminé'
                }
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
            slackSend channel: '#jenkins', message: "Build Success - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)", teamDomain: 'jenkins-55k4809', tokenCredentialId: 'slack-cred'
        }
        failure {
            echo 'Pipeline failed!'
            slackSend channel: '#jenkins', message: "Build Failed - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)", teamDomain: 'jenkins-55k4809', tokenCredentialId: 'slack-cred'
        }
    }
} // <-- fin du pipeline
