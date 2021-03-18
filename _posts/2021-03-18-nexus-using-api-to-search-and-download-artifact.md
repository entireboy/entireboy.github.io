---
layout: post
title:  "[Nexus] API를 통한 artifact 검색 / 다운로드"
date:   2021-03-18 22:18:00 +0900
published: true
categories: [ nexus ]
tags: [ nexus, api, search, download, artifact, asset, script ]
---

Nexus API를 통해서 artifact를 다운로드 받을 수 있다. 스크립트 등을 통해 현재 업로드 되어 있는 버전 정보를 가져오고 그 버전의 artifact를 다운 받는 것들도 가능하다.


# Nexus 3.x 버전

Nexus가 3.x 버전 부터는 많은 API를 제공한다. 특히 검색 API([Search API](https://help.sonatype.com/repomanager3/rest-and-integration-api/search-api))도 있고, [artifact 다운로드](https://help.sonatype.com/repomanager3/rest-and-integration-api/search-api#SearchAPI-SearchandDownloadAsset)) 까지 제공하고 있어서 스크립트에서 유용하게 쓸 수 있다.

```bash
GET /service/rest/v1/search/assets/download
```


## 최신 버전 다운로드

(이 기능은 [Nexus 3.16.0 버전 이상에서만 제공](https://help.sonatype.com/repomanager3/rest-and-integration-api/search-api#SearchAPI-DownloadingtheLatestVersionofanAsset)한다.)

최신 버전 artifact는 다음 경로에서 받을 수 있으며,

```bash
http://[NEXUS_URL]/service/rest/v1/search/assets/download?sort=version&repository=maven-snapshots&maven.groupId=org.foo.bar&maven.artifactId=project&maven.extension=jar
```

아래와 같은 스크립트를 만들어서 사용할 수 있다. `sort=version` 파라미터로 버전 기준으로 정렬을 할 수 있어서 최신 버전을 가져올 수 있다.

```bash
response=$(curl -s -w "%{http_code}" -o leocat-batch.jar -L -X GET "https://nexus.leocat.kr/service/rest/v1/search/assets/download?sort=version&repository=maven-snapshot&maven.groupId=kr.leocat&maven.artifactId=leocat-batch&maven.extension=jar" -H "accept: application/json")
if [ "$response" != "200" ]
then
 exit 1
fi
```

`-w "%{http_code}"` 는 `curl` 실행 결과로 http status code 를 출력하도록 하는 것이다. 그 값이 정상(200)인지 체크하기 위함이다.


## 특정 버전 다운로드

특정 버전 artifact를 다운 받으려면 `maven.baseVersion=1.2.3-SNAPSHOT` 처럼 버전을 명시하면 된다.

```bash
http://[NEXUS_URL]/service/rest/v1/search/assets/download?sort=version&repository=maven-snapshots&maven.groupId=org.foo.bar&maven.artifactId=project&maven.baseVersionmaven.baseVersion=1.2.3-SNAPSHOT&maven.extension=jar
```

아래와 같은 스크립트로 사용할 수 있다. `${BATCH_VERSION}` 은 전달인자 등으로 받아서 버전 정보를 지정한다.

```bash
response=$(curl -s -w "%{http_code}" -o leocat-batch.jar -L -X GET "https://nexus.leocat.kr/service/rest/v1/search/assets/download?sort=version&repository=maven-snapshot&maven.groupId=kr.leocat&maven.artifactId=leocat-batch&maven.baseVersion=${BATCH_VERSION}&maven.extension=jar" -H "accept: application/json")
if [ "$response" != "200" ]
then
 exit 1
fi
```


# Nexus 2.x 버전

API를 제공하지 않기 때문에 리스팅 xml 파일 내용을 파싱해서 사용할 수 있다. (팀에서 이런 방식을 사용했었다.)


## 최신 버전 알아오기

버전 정보를 리스팅해주는 xml 내용에서 버전 부분만 자르고 정렬해서 최신 버전을 구한다. (새삼 Nexus 3 버전에서 제공하는 검색 API가 멋있어 보인다.)

```bash
LATEST_BATCH_JAR_VERSION=$(curl -s "http://nexus.leocat.kr/content/repositories/snapshots/kr/leocat/leocat-batch/leocat-batch-jar/maven-metadata.xml" | grep -Po '(?<=<version>)([0-9\.]+(-SNAPSHOT)?)' | sort --version-sort -r| head -n 1)
echo "Latest build version : ${LATEST_BATCH_JAR_VERSION}"
echo "BATCH_VERSION=${LATEST_BATCH_JAR_VERSION}" > version.properties
```


## 특정 버전 다운로드

`v` 파라미터로 버전을 명시하면 된다. 마찬가지로 `${BATCH_VERSION}` 부분은 스크립트 파라미터 등으로 받아온다.

```bash
response=$(curl -s -w "%{http_code}" -o leocat-batch.jar "http://nexus.leocat.kr/service/local/artifact/maven/content?r=snapshots&g=kr.leocat.leocat-batch&a=leocat-batch-jar&e=jar&v=${BATCH_VERSION}")
if [ "$response" != "200" ]
then
 exit 1
fi
```


# 참고

- [Search API - Nexus documentation](https://help.sonatype.com/repomanager3/rest-and-integration-api/search-api)
