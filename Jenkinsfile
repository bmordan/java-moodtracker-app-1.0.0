pipeline {
    agent {
        docker {
            image 'gradle:7.6.0-jdk17'
        }
    }
    environment {
        MOD2_AUTH0_CLIENT_ID = credentials("MOD2_AUTH0_CLIENT_ID")
        MOD2_AUTH0_CLIENT_SECRET = credentials("MOD2_AUTH0_CLIENT_SECRET")
        MOD2_AUTH0_ISSUER = credentials("MOD2_AUTH0_ISSUER")
    }
    stages {
        stage("hello-github") {
            steps {
                git credentialsId: 'github', url: 'https://github.com/MultiverseLearningProducts/java-moodtracker-app'
                sh 'java --version'
                sh 'gradle --version'
            }
        }
        stage("Test") {
            steps {
                sh 'gradle clean test'
                sh 'gradle bootJar'
            }
        }
    }
}