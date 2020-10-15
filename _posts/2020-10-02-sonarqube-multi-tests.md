---
layout: post
title:  "[SonarQube] (integration test 등) test가 여러개인 경우"
date:   2020-10-02 21:18:00 +0900
published: true
categories: [ sonarqube ]
tags: [ sonarqube, multi, test, integration test ]
---

팀에서 [SonarQube](https://www.sonarqube.org/)를 통해 Java 코드의 code coverage와 code smell 등을 확인하고 있다. (Quality gate를 설정하고 통과하지 못 하면 올스탑 그리고 해결 후 비즈니스 개발 시작) Code coverage는 JaCoCo report를 통해서 체크하는데, unit test와 integration test가 별도의 경로로 분리되어 생성되고 있다. 각각 이 경로로 test report를 만들고 있다.

- {subproject}/build/jacoco/jacoco.exec
- {subproject}/build/jacoco/jacoco-integration.exec

대략적인 gradle 설정은 아래와 같다. unit test로 `test` task를 사용하고, integration test로 `integrationTest` task를 별도로 만들어서 사용하고 있다. 때문에 각각 test report가 각각 생성된다. 그 경로는 JaCoCo 플러그인의 `destinationFile` 설정으로 하고 있다.

```groovy
jacoco {
    toolVersion = "0.8.5"
}

test {
  jacoco {
      enabled = true
      destinationFile = file("${buildDir}/jacoco/jacoco.exec")
  }
}

task integrationTest(type: Test) {
    description = 'Runs integration tests.'
    group = 'verification'

    testClassesDirs = sourceSets.integrationTest.output.classesDirs
    classpath = sourceSets.integrationTest.runtimeClasspath

    jacoco {
        enabled = true
        destinationFile = file("${buildDir}/jacoco/jacoco-integration.exec")
    }
}

check.dependsOn integrationTest
```


# 문제

SonarQube gradle plugin이 3.0으로 버전업 되면서 JaCoCo report는 `xml 타입`만 허용하도록 변경되었다. 기존 버전에서 사용하던 [`sonar.jacoco.reportPaths`설정은 deprecated 돼서 `sonar.coverage.jacoco.xmlReportPaths`를 사용]해야 한다.([https://docs.sonarqube.org/latest/analysis/coverage/](https://docs.sonarqube.org/latest/analysis/coverage/)).

이를 위해, `jacocoTestReport` task를 실행해서 `xml 타입`의 JaCoCo test report를 생성해야 한다.([The JaCoCo Plugin]([https://docs.gradle.org/current/userguide/jacoco_plugin.html](https://docs.gradle.org/current/userguide/jacoco_plugin.html))) 그런데 아래처럼 설정하면 `test` task로 실행한 unit test 결과는 report에 잘 나오는데, 별도로 생성한 `integrationTest` task로 실행한 integration test 결과는 xml report에 포함되지 않는 문제가 생긴다.

```groovy
jacocoTestReport {
    reports {
        xml.enabled = true
        xml.destination = file("${buildDir}/jacoco/jacoco.xml")
    }
    dependsOn(test, integrationTest)
}

sonarqube {
    properties {
        property "sonar.sources", "src/main/java"
        property "sonar.junit.reportPaths", "${buildDir}/test-results/test,${buildDir}/test-results/integrationTest"
//      sonar.jacoco.reportPaths는 deprecated 됨
//      property "sonar.jacoco.reportPaths", "${buildDir}/jacoco/jacoco.exec,${buildDir}/jacoco/jacoco-integration.exec"
        property "sonar.coverage.jacoco.xmlReportPaths", "${buildDir}/jacoco/jacoco.xml"
    }
}
```


# 해결

`jacocoTestReport` task에서 사용할 `exec 타입`의 테스트를 모두 포함할 수 있도록 `executionData`를 include해 주면 된다. 여기서 포인트는 `include`로, 파일이 존재하는 경우만 추가하는 것이다.

```groovy
jacocoTestReport {
    executionData(fileTree(buildDir).include("/jacoco/*.exec"))
    reports {
        xml.enabled = true
        xml.destination = file("${buildDir}/jacoco/jacoco.xml")
    }
    dependsOn(test, integrationTest)
}
```

자!! 그럼 테스트와 SonarQube로 분석 시작!!

```bash
$ ./gradlew clean check jacocoTestReport sonarqube
```


# 주의

아래처럼 `executionData`를 설정해도 되지만, 멀티 모듈 설정의 프로젝트에서 하위 모듈 중에 하나라도 경로가 없으면 실패하게 된다. 일부 모듈은 integration test가 없어서 만들지 않았지만, 아래처럼 설정하면 테스트 결과 report가 없어서 실패하게 된다.

```groovy
jacocoTestReport {
    executionData tasks.withType(Test)
    reports {
        xml.enabled = true
        xml.destination = file("${buildDir}/jacoco/jacoco.xml")
    }
    dependsOn(test, integrationTest)
}
```

아래와 같이 새성되지 않은 report를 찾지 못 하는 오류가 발생한다.

```groovy
$ ./gradlew clean check jacocoTestReport
    .. (생략) ..

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':SUB_MODULE:jacocoTestReport'.
> Unable to read execution data file /PATH_TO_PROJECT/SUB_MODULE/build/jacoco/jacoco-integration.exec
```


# 참고

- [Test Coverage & Execution]([https://docs.sonarqube.org/latest/analysis/coverage/](https://docs.sonarqube.org/latest/analysis/coverage/))
- [The JaCoCo Plugin]([https://docs.gradle.org/current/userguide/jacoco_plugin.html](https://docs.gradle.org/current/userguide/jacoco_plugin.html))
