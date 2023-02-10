pipeline {
    agent none
    environment {
        MOD2_AUTH0_CLIENT_ID = credentials("MOD2_AUTH0_CLIENT_ID")
        MOD2_AUTH0_CLIENT_SECRET = credentials("MOD2_AUTH0_CLIENT_SECRET")
        MOD2_AUTH0_ISSUER = credentials("MOD2_AUTH0_ISSUER")
    }
    stages {
        stage("Test") {
            agent {
                docker {
                    image 'gradle:7.6.0-jdk17'
                }
            }
            steps {
                sh 'gradle clean test'
                sh 'gradle bootJar'
                sh 'ls -l'
                stash includes: 'Dockerfile', name: 'Dockerfile'
                stash includes: 'build/libs/*.jar', name: 'jarfile'
            }
        }
        stage("Dockerize") {
            agent {
                docker {
                    image 'docker:dind'
                }
            }
            steps {
                unstash 'Dockerfile'
                unstash 'jarfile'
                sh 'ls -l'
                sh 'docker build -t bmordan/moodtracker .'
                sh 'docker push bmordan/moodtracker'
            }
        }
        stage("Deploy") {
            agent any
            steps {
                sh 'ls -l build/libs/*.jar'
                sshagent(credentials: ["AWS-DOCKER-COMPOSE"]) {
                    sh "ssh -o StrictHostKeyChecking=no ec2-user@13.42.55.28 'docker --version'"
                    sh "ssh -o StrictHostKeyChecking=no ec2-user@13.42.55.28 'docker-compose --version'"
                    sh "ssh -o StrictHostKeyChecking=no ec2-user@13.42.55.28 'docker stop moodtracker || true && docker rm moodtracker || true'"
                    sh "ssh -o StrictHostKeyChecking=no ec2-user@13.42.55.28 'docker run -e MOD2_AUTH0_CLIENT_ID=${MOD2_AUTH0_CLIENT_ID} -e MOD2_AUTH0_CLIENT_SECRET=${MOD2_AUTH0_CLIENT_SECRET} -e MOD2_AUTH0_ISSUER=${MOD2_AUTH0_ISSUER} -name=moodtracker -p 8080:8080 -d bmordan/moodtracker'"
                }
            }
        }
    }
}