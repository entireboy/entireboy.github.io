---
layout: post
title:  "[Jenkins] Unknown stage section 오류"
date:   2020-02-17 22:18:00 +0900
published: true
categories: [ jenkins ]
tags: [ jenkins, pipeline, stage, step, syntax ]
---

# 문제

Jenkins pipeline 설정을 테스트할 때, job을 실행시키면 `Unknown stage section`와 같은 오류를 만날 수 있다.

아래 코드는 SonarQube [Jenkins pipeline 설정 매뉴얼](https://docs.sonarqube.org/7.9/analysis/scan/sonarscanner-for-jenkins/)에 있는 내용이다. (믿고 썼는데 오류가;;;)

```groovy
pipeline {
  agent any
  stages {
    stage('SonarQube analysis') {
      withSonarQubeEnv('sonarqube') { // Will pick the global server connection you have configured
        sh './gradlew clean check sonarqube'
      }
    }

    stage("Quality Gate"){
      timeout(time: 1, unit: 'HOURS') { // Just in case something goes wrong, pipeline will be killed after a timeout
        script {
          def qg = waitForQualityGate() // Reuse taskId previously collected by withSonarQubeEnv
          if (qg.status != 'OK') {
            error "Pipeline aborted due to quality gate failure: ${qg.status}"
          }
        }
      }
    }
  }
}
```

Jenkins job을 실행하면 이런 오류가 나온다.

```bash
First time build. Skipping changelog.
Running in Durability level: MAX_SURVIVABILITY
org.codehaus.groovy.control.MultipleCompilationErrorsException: startup failed:
WorkflowScript: 9: Unknown stage section "withSonarQubeEnv". Starting with version 0.5, steps in a stage must be in a ‘steps’ block. @ line 9, column 9.
           stage('SonarQube analysis') {
           ^

WorkflowScript: 29: Expected one of "steps", "stages", or "parallel" for stage "Quality Gate" @ line 29, column 9.
          stage("Quality Gate"){
```


# 해결방법

Declarative Pipeline에서 `stage`를 만들 때 `steps`를 나열해 줘야 한다. `if`문과 같은 Scripted Pipeline을 사용하게 되는 경우는 `script`로 감싸줘야 한다.

```groovy
pipeline {
  agent any
  stages {
    stage('SonarQube analysis') {
      steps {
        withSonarQubeEnv('sonarqube') { // Will pick the global server connection you have configured
          sh './gradlew clean check sonarqube'
        }
      }
    }

    stage("Quality Gate"){
      steps {
        timeout(time: 1, unit: 'HOURS') { // Just in case something goes wrong, pipeline will be killed after a timeout
          script {
            def qg = waitForQualityGate() // Reuse taskId previously collected by withSonarQubeEnv
            if (qg.status != 'OK') {
              error "Pipeline aborted due to quality gate failure: ${qg.status}"
            }
          }
        }
      }
    }
  }
}
```


# 참고

- [Pipeline Syntax - Jenkins docs](https://jenkins.io/doc/book/pipeline/syntax/#declarative-steps)
- [Unknown stage section "withSonarQubeEnv" - stackoverflow](https://stackoverflow.com/questions/42763384/unknown-stage-section-withsonarqubeenv)
- [SonarQube Scanner for Jenkins - Jenkins docs](https://jenkins.io/doc/pipeline/steps/sonar/)
