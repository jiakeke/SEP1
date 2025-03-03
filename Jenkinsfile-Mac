pipeline {
    agent any
    stages {
        stage('checking') {
            steps {
                git branch: 'main', url: 'https://github.com/jiakeke/SEP1.git'
            }
        }
        stage('build') {
            steps {
                sh 'mvn -f GradeBook/pom.xml clean install'
            }
        }

        stage('Test & Coverage') {
            steps {
                sh 'mvn -f GradeBook/pom.xml test jacoco:report' // Run tests and generate a JaCoCo coverage report
            }

            post {
                always {
                    junit 'GradeBook/target/surefire-reports/*.xml' // Publish JUnit test results
                    jacoco execPattern: '**/target/jacoco.exec', // Reads JaCoCo execution file
                           classPattern: '**/target/classes',
                           sourcePattern: '**/src/main/java',
                           exclusionPattern: '**/test/**'
                }
            }

        }
    }
}
