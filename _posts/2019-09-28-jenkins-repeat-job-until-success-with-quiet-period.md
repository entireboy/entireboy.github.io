---
layout: post
title:  "[Jenkins] 성공할 때 까지 잡 반복시키기 - quiet period(delay)와 함께"
date:   2019-09-28 23:18:00 +0900
published: true
categories: [ jenkins ]
tags: [ jenkins, repeat, job, quiet period, delay, until, status, stable, unstable ]
---

# 문제

동일한 jenkins job을 성공할 때 까지 반복시키고 싶다. 예를 들어, 다른 작업의 종료여부를 모니터링하는 job이 있는데, 특정 시간부터 다른 작업이 종료됐는지 5분 마다 한번씩 체크한다. 그리고 그 작업이 종료되면 이 모니터링 job도 함께 종료한다.


# 문제

Jenkins job을 [REST API](https://wiki.jenkins.io/display/JENKINS/Remote+access+API)로 원격에서 실행시키고 싶다. 오늘의 문제는 **로그인 인증이 필요** 하다는 것이다. 인증을 쓰는 경우 job 설정으로 `빌드를 원격으로 유발(Trigger builds remotely)` > `Authentication Token`을 설정해 줘도 로그인이 필요하다.


# 해결방법

[Build Token Root plugin](https://wiki.jenkins.io/display/JENKINS/Build+Token+Root+Plugin)을 설치하고, remote trigger로 사용할 token을 지정한다. 플러그인이 로그인 인증을 사용하는 대신 이 token을 인증으로 사용할 수 있는 별도의 URL을 제공한다. 때문에 token을 지정하지 않으면 호출할 수 없다.


## Build Token Root plugin

Build Token Root Plugin(https://wiki.jenkins.io/display/JENKINS/Build+Token+Root+Plugin)을 설치한다.

{% include image.html file='/assets/img/2019-06-21-jenkins-remote-triggering-without-authentication1.png' alt='Build Token Root Plugin' %}

> **NOTE**: 플러그인 이름은 `Build Token Root Plugin`이지만 Jenkins 프러그인 검색할 때는 `Build Authorization Token Root`로 검색해야 한다. (한참 찾았어;;)


## Token 설정

사용하는 각 job 마다 job 설정에서 remote trigger token을 설정한다.

`Job 설정` > `빌드 유발` > `빌드를 원격으로 유발` > `Authentication Token`

{% include image.html file='/assets/img/2019-06-21-jenkins-remote-triggering-without-authentication2.png' alt='Authentication Token' %}


## 호출 URL

기존 REST API 대신 플러그인이 제공하는 별도의 URL로 호출해야 한다. 호출할 때 query string으로 token을 함께 넘겨주고, parameter가 있을 때도 함께 넘겨 주면 된다.

```
// parameter 없는 경우
{JENKINS_URL}/job/{JOB_NAME}/build?token={TOKEN}
  ->
{JENKINS_URL}/buildByToken/build?job={JOB_NAME}&token={TOKEN}


// parameter 있는 경우
{JENKINS_URL}/job/{JOB_NAME}/buildWithParameters?token={TOKEN}
  ->
{JENKINS_URL}/buildByToken/buildWithParameters?job={JOB_NAME}&token={TOKEN}&{KEY1}={VALUE1}&{KEY2}={VALUE2}
```


# 참고

- [Build Token Root plugin](https://wiki.jenkins.io/display/JENKINS/Build+Token+Root+Plugin)
- [[Jenkins] Remote API로 빌드 시 바로 실행되지 않고 pending되는 현상](https://blog.leocat.kr/notes/2018/06/08/jenkins-quiet-period-on-calling-remote-api)
