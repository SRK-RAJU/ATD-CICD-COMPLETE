pipeline {
    agent any
    tools{
        maven 'maven3'
    }
    stages {
        stage('git-checkout') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[credentialsId: 'raju-github-ssh', url: 'git@github.com:SRK-RAJU/ATD-CICD-COMPLETE.git']])
            }
        }
    }
}
