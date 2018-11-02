---
layout: post
title:  "[nexus] Maven을 이용한 jar 파일 업로드(publish)"
date:   2018-11-01 22:18:00 +0900
published: true
categories: [ maven ]
tags: [ nexus, repository, artifact , maven, upload, jar, publish ]
---

> NOTE: Gradle을 사용하면 [[nexus] Gradle을 이용한 jar 파일 업로드(publish)]({{ site.baseurl }}{% post_url 2018-11-01-nexus-publish-jar-artifact-with-gradle %}) 참고

외부에서 제공하는 jar 파일을 nexus나 JFrog 같은 repository에 업로드 해야 하는 경우가 있다.

nexus UI 등을 통해서 업로드하면 진짜 편하지만, 회사에서 공용으로 운영하는 nexus 같은 경우는 관리상의 이유로 (지금 나처럼..) UI 권한이 없을 수도 있다. nexus REST API를 curl 등으로 호출하는 방법도 있지만, 역시나 뒤죽박죽 될 수 있기 때문에 열어두지 않는 경우도 있다.

이럴 때 maven을 사용하면, 간단히 업로드가 가능하다. 보통 라이브러리 형태의 프로젝트들은 배포하면서 release 버전을 찍어 업로드하기 때문에 gradle이나 maven 등으로 배포를 할테니 이걸 막아두지는 않을 것이다.

```bash
$ mvn \
    deploy:deploy-file \
    -DgroupId=kr.leocat \
    -DartifactId=publish-sample \
    -Dversion=1.4 \
    -DgeneratePom=true \
    -Dpackaging=jar \
    -Durl=http://repo.my.host/content/repositories/releases/ \
    -Dfile=publish-sample-1.4.jar
```

`-DgeneratePom=true` 옵션은 pom 파일을 자동으로 생성해 준다.

# Authentication

인증이 필요한 경우 아래 설정을 넣어 주고 `-DrepositoryId=mynexus` 옵션도 함께 주면 된다.

```xml
<servers>
...
  <server>
    <id>mynexus</id>
    <username>deployment</username>
    <password>deployment123</password>
  </server>
</servers>
```


# 참고

- [[nexus] Gradle을 이용한 jar 파일 업로드(publish)]({{ site.baseurl }}{% post_url 2018-11-01-nexus-publish-jar-artifact-with-gradle %})
- [How can I programmatically upload an artifact into Nexus 3?](https://support.sonatype.com/hc/en-us/articles/115006744008-How-can-I-programmatically-upload-an-artifact-into-Nexus-3-)
