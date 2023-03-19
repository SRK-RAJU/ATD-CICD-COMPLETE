pipeline {
    agent any
    tools {
        maven 'maven3'
    }

//    environment {
//        NEXUS_URL = "34.131.206.134:8081"
//        NEXUS_REPOSITORY = "raju"
//        NEXUS_CREDENTIAL_ID = "nexus-jenkins"
//        NEXUS_VERSION = "nexus3"
//        NEXUS_PROTOCOL = "http"
//
//    }
    stages {
        stage('git-checkout') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], extensions: [], gitTool: 'git', userRemoteConfigs: [[credentialsId: '37fde340-f3c4-412f-8f7f-49b60b98a4ee', url: 'git@github.com:SRK-RAJU/ATD-CICD-COMPLETE.git']])
            }
        }
        stage('maven-build') {
            steps {
                sh '''
            cd $WORKSPACE/springboot/java-springboot
            mvn clean package
            mvn package -DskipTests=true
            '''
            }
        }

//        stage("Publish to Nexus Repository Manager") {
//            steps {
//                script {
//                    def pom = readMavenPom file: "pom.xml";
//                    filesByGlob = findFiles(glob: "target/*.${pom.packaging}");
//                    echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}"
//                    artifactPath = filesByGlob[0].path;
//                    artifactExists = fileExists artifactPath;
//                    if (artifactExists) {
//                        echo "*** File: ${artifactPath}, group: ${pom.groupId}, packaging: ${pom.packaging}, version ${pom.version}";
//                        nexusArtifactUploader(
//                                nexusVersion: NEXUS_VERSION,
//                                protocol: NEXUS_PROTOCOL,
//                                nexusUrl: NEXUS_URL,
//                                groupId: pom.groupId,
//                                version: pom.version,
//                                repository: NEXUS_REPOSITORY,
//                                credentialsId: NEXUS_CREDENTIAL_ID,
//                                artifacts: [
//                                        [artifactId: pom.artifactId,
//                                         classifier: '',
//                                         file      : artifactPath,
//                                         type      : pom.packaging],
//                                        [artifactId: pom.artifactId,
//                                         classifier: '',
//                                         file      : "pom.xml",
//                                         type      : "pom"]
//                                ]
//                        );
//                    } else {
//                        error "*** File: ${artifactPath}, could not be found";
//                    }
//                }
//            }
//        }


        stage("Publish to Nexus Repository Manager") {
            steps {
                script {
                    def pom = readMavenPom file: "pom.xml";
                    nexusArtifactUploader  artifacts: [
                                    [ artifactId: pom.artifactId,
                                     classifier: '',
                                     file      : "target/springboot-backend-${pom.version}.jar" ,
                                     type      : pom.packaging]
                    ],
                            nexusVersion: "nexus3",
                               protocol: "http",
                               nexusUrl: "34.131.206.134:8081",
                                groupId: pom.groupId,
                                version:  "${pom.version}",
                                repository: "raju",
                                credentialsId: "nexus-jenkins"

                }
            }

            }
        }



    }



pipeline {
    agent any
    tools {
        maven 'maven3'
    }

    environment {
        NEXUS_VERSION = "nexus3"
        NEXUS_PROTOCOL = "http"
        NEXUS_URL = "34.131.206.134:8081"
        NEXUS_REPOSITORY = "raju"
        NEXUS_CREDENTIAL_ID = "nexus-jenkins"
    }
    stages {
        stage("Clone code from VCS") {
            steps {
                script {
                    git 'https://github.com/SRK-RAJU/ATD-CICD-COMPLETE.git';
                }
            }
        }
        stage("Maven Build") {
            steps {
                script {
                    sh ''' mvn clean package 
  "mvn package -DskipTests=true" 
 '''
                }
            }
        }
        stage("Publish to Nexus Repository Manager") {
            steps {
                script {
                    pom = readMavenPom file: "pom.xml";
                    filesByGlob = findFiles(glob: "target/*.${pom.packaging}");
                    echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}"
                    artifactPath = filesByGlob[0].path;
                    artifactExists = fileExists artifactPath;
                    if(artifactExists) {
                        echo "*** File: ${artifactPath}, group: ${pom.groupId}, packaging: ${pom.packaging}, version ${pom.version}";
                        nexusArtifactUploader(
                                nexusVersion: NEXUS_VERSION,
                                protocol: NEXUS_PROTOCOL,
                                nexusUrl: NEXUS_URL,
                                groupId: pom.groupId,
                                version: pom.version,
                                repository: NEXUS_REPOSITORY,
                                credentialsId: NEXUS_CREDENTIAL_ID,
                                artifacts: [
                                        [artifactId: pom.artifactId,
                                         classifier: '',
                                         file: artifactPath,
                                         type: pom.packaging],
                                        [artifactId: pom.artifactId,
                                         classifier: '',
                                         file: "pom.xml",
                                         type: "pom"]
                                ]
                        );
                    } else {
                        error "*** File: ${artifactPath}, could not be found";
                    }
                }
            }
        }
    }
}


nexusArtifactUploader artifacts: [[artifactId: 'springboot-backend', classifier: '', file: 'target/*.jar', type: 'jar']], credentialsId: 'nexus-jenkins', groupId: 'net.javaguides', nexusUrl: '34.131.206.134:8081', nexusVersion: 'nexus3', protocol: 'http', repository: 'raju', version: '0.0.1-SNAPSHOT'

stage('nexus-integration') {
    steps {
        nexusArtifactUploader artifacts: [[artifactId: 'springboot-backend', classifier: '', file: 'target/*.jar', type: 'jar']], credentialsId: 'nexus-jenkins', groupId: 'net.javaguides', nexusUrl: '34.131.206.134:8081', nexusVersion: 'nexus3', protocol: 'http', repository: 'raju', version: '0.0.1-SNAPSHOT'
    }
}

