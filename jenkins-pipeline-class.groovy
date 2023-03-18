pipeline {
    agent any
    environment {
      DOCKERHUB_CREDENTIALS = credentials('docker-cred')
    }
    tools{ 
        maven 'maven3'
    }
    stages {
        stage('git-checkout') {
            steps {
                checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'jenkins', url: 'git@github.com:sathish441/sathish.git']])
            }
        }
        stage('maven-build') {
            steps {
            sh '''
            cd $WORKSPACE/springboot/java-springboot
            mvn clean package
            '''
            }
        }
        stage('sonar') {
            steps {
                sh '''
            cd $WORKSPACE/springboot/java-springboot 
            mvn sonar:sonar \
            -Dsonar.projectKey=sathish \
            -Dsonar.host.url=http://44.206.253.72:9000 \
            -Dsonar.login=0075ac35a1cf2bf6889ce2c1b369288f42a8f94f 
              '''
            }
        }
        stage('nexus-integration') {
            steps {
            nexusArtifactUploader artifacts: [[artifactId: 'springboot-backend', classifier: '', file: 'springboot/java-springboot/target/springboot-backend-0.0.1-SNAPSHOT.jar', type: 'jar']], credentialsId: 'nexus-id', groupId: 'net.javaguides', nexusUrl: '3.82.252.85:8081', nexusVersion: 'nexus3', protocol: 'http', repository: 'sathish', version: '0.0.1-SNAPSHOT'
            }
        }
        stage('Docker-build') {
           steps {
             sh '''
             echo "$DOCKERHUB_CREDENTIALS_PSW" | docker login -u "$DOCKERHUB_CREDENTIALS_USR" --password-stdin
             docker build -t sathish441/createat-devops-task:spring-v1 $WORKSPACE/springboot/java-springboot/.
             docker image prune -f 
             '''
           }
        }
        stage('Docker-push') {
           steps {
             sh '''
                docker push sathish441/createat-devops-task:spring-v1 
             '''
           }
        }
    }
}