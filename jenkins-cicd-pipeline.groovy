pipeline {
    agent any
    environment {
        DOCKERHUB_CREDENTIALS = credentials('13d8215c-9ad9-4023-ad47-2ca6b56f51ff')
    }
    tools {
        maven 'maven3'
    }

    stages {
        stage("Clone code from VCS") {
            steps {
                script {
                    git branch: 'main' , url: 'https://github.com/SRK-RAJU/ATD-CICD-COMPLETE.git';
                }
            }
        }
        stage("Maven Build") {
            steps {
                script {
                    sh '''
            cd $WORKSPACE/springboot/java-springboot
            mvn clean package
            mvn package -DskipTests=true
            '''

                }
            }
        }
        stage('nexus-integration') {
            steps {
                nexusArtifactUploader artifacts: [[artifactId: 'springboot-backend', classifier: '', file: 'springboot/java-springboot/target/springboot-backend-0.0.1-SNAPSHOT.jar', type: 'jar']], credentialsId: 'nexus-jenkins', groupId: 'net.javaguides', nexusUrl: '35.238.20.94:8081', nexusVersion: 'nexus3', protocol: 'http', repository: 'raju', version: '0.0.1-SNAPSHOT'    }
        }
        stage('sonar') {
            steps {
                sh '''
            cd $WORKSPACE/springboot/java-springboot 
           mvn clean verify sonar:sonar \
  -Dsonar.projectKey=raju_sree \
  -Dsonar.host.url=http://34.131.6.152:9000 \
  -Dsonar.login=sqp_5858a12a118260efcf56b5a023d85747d591b8c3
  
              '''
            }
        }

        stage('Docker-build') {
            steps {
                sh '''
             echo "$DOCKERHUB_CREDENTIALS_PSW" | docker login -u "$DOCKERHUB_CREDENTIALS_USR" --password-stdin
             docker build -t srkmsk/atd-cicd:spring-v1 $WORKSPACE/springboot/java-springboot/.
             docker image prune -f 
             '''
            }
        }

        stage('Docker-push') {
            steps {
                sh '''
                docker push srkmsk/atd-cicd:spring-v1 
             '''
            }
        }

    }
}
