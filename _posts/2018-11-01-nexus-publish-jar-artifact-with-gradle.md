---
layout: post
title:  "[nexus] Gradle을 이용한 jar 파일 업로드(publish)"
date:   2018-11-01 23:18:00 +0900
published: true
categories: [ maven ]
tags: [ nexus, repository, artifact , maven, upload, jar, publish, gradle ]
---

> NOTE: Maven을 사용하면 [[nexus] Maven을 이용한 jar 파일 업로드(publish)]({{ site.baseurl }}{% post_url 2018-11-01-nexus-publish-jar-artifact-with-maven %}) 참고

외부에서 제공하는 jar 파일을 nexus나 JFrog 같은 repository에 업로드 해야 하는 경우가 있다. (역시나 Maven글과 유사한 이유.. gradle 프로젝트에서 사용하기 위해 시간날 때 해보고 내용정리)

nexus UI 등을 통해서 업로드하면 진짜 편하지만, 회사에서 공용으로 운영하는 nexus 같은 경우는 관리상의 이유로 (지금 나처럼..) UI 권한이 없을 수도 있다. nexus REST API를 curl 등으로 호출하는 방법도 있지만, 역시나 뒤죽박죽 될 수 있기 때문에 열어두지 않는 경우도 있다.

이럴 때 gradle을 사용하면, 간단히 업로드가 가능하다. 보통 라이브러리 형태의 프로젝트들은 배포하면서 release 버전을 찍어 업로드하기 때문에 gradle이나 maven 등으로 배포를 할테니 이걸 막아두지는 않을 것이다.

사실, 이 예제는 gradle을 사용하긴 했지만 [maven-publish plugin](https://docs.gradle.org/current/userguide/publishing_maven.html)을 사용한 방법이니, maven을 사용했다고 해야 하나..?? 여튼 gradle로 publishing하는 `build.gradle` 샘플..

```groovy
plugins {
  id 'maven-publish'
}

publishing {
  publications {
    maven(MavenPublication) {
      groupId = 'kr.leocat'
      artifactId = 'publish-sample'
      version = '1.4'


      artifact 'publish-sample-1.4.jar'
    }
  }
  repositories {
    maven {
      url = 'http://repo.my.host/content/repositories/releases/'
    }
  }
}
```

`publish`나 `publishToMavenLocal` 등의 task를 실행하면 된다. (task 리스트는 [Maven Publish Plugin - Gradle Docs](https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven:tasks) 참고)

```bash
$ ./gradlew publish
```

`build.gradle` 파일의 `artifact 'publish-sample-1.4.jar'` 부분에 artifact에 첨부할 것들을 아래처럼 리스팅해 주면 된다.

```groovy
publishing {
  publications {
    maven(MavenPublication) {
      artifact sourceJar // Publish the output of the sourceJar task
      artifact 'my-file-name.jar' // Publish a file created outside of the build
      artifact source: sourceJar, classifier: 'src', extension: 'zip'
    }
  }
}
```

POM 파일은 자동으로 생성된다. POM 파일에 라이선스나 연락처 등을 지정해 주고 싶다면
[Maven Publish Plugin - Gradle Docs](https://docs.gradle.org/current/userguide/publishing_maven.html#sec:modifying_the_generated_pom) 참고하자.


# 참고

- [[nexus] Maven을 이용한 jar 파일 업로드(publish)]({{ site.baseurl }}{% post_url 2018-11-01-nexus-publish-jar-artifact-with-maven %})
- [Maven Publish Plugin - Gradle Docs](https://docs.gradle.org/current/userguide/publishing_maven.html)
- [MavenPublication - Gradle Docs](https://docs.gradle.org/current/dsl/org.gradle.api.publish.maven.MavenPublication.html)
