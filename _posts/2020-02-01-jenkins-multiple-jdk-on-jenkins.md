---
layout: post
title:  "[Jenkins] 여러 JDK 설치해서 골라 사용하기"
date:   2020-02-01 21:18:00 +0900
published: true
categories: [ jenkins ]
tags: [ jenkins, multiple, jdk, config, use, job ]
---

Jenkins에서 각 job 마다 실행할 JDK 버전이 다른 경우가 있다. 각 job의 언어가 다르거나, 각 프로젝트 단위로 JDK 버전업을 진행하고 있을 수 있다. 이럴 때는 Jenkins에 JDK를 설정해두고 선택해서 사용할 수 있다.


# JDK 설치

원하는 JDK를 설치한다. 끗. (읭??)


# Jenkins에 JDK 경로 추가

Jenkins에 설치한 JDK 경로를 추가해 주기 위해 `Global Tool Configuration` 화면으로 이동한다. 오래된 Jenkins 버전의 경우는 `Global Tool Configuration`이 아닌 `Configure System (시스템 설정)`에 JDK 설정 메뉴가 있다.

```
Jenkins 설정 > Global Tool Configuration > JDK > JDK installation
```


{% include image.html file='/assets/img/2020-02-01-jenkins-multiple-jdk-on-jenkins1.png' width='300px' %}
{% include image.html file='/assets/img/2020-02-01-jenkins-multiple-jdk-on-jenkins2.png' width='450px' %}

JDK 섹션에서 `ADD JDK`를 클릭해서 골라서 사용할 JDK를 등록한다.

{% include image.html file='/assets/img/2020-02-01-jenkins-multiple-jdk-on-jenkins3.png' %}

아래처럼 `Install automatically`를 선택하면 JDK를 수동으로 설치하지 않아도 된다. 하지만 여기서 설치되는 JDK는 Oracle JDK이고, 무료 버전인 9 까지만 지원하고 유료를 쓰는 경우는 별도의 계정 설정이 필요하다.

{% include image.html file='/assets/img/2020-02-01-jenkins-multiple-jdk-on-jenkins4.png' %}


# Job 설정

이제 job 설정에서 실행할 JDK를 선택하면 된다. `Global Tool Configuration`에서 설정한 JDK 이름이 보인다.

{% include image.html file='/assets/img/2020-02-01-jenkins-multiple-jdk-on-jenkins5.png' width='450px' %}


# Jenkinsfile을 사용하는 경우 (pipeline)

Jenkinsfile을 사용하는 경우에는 아래 설정을 추가하면 된다. `Global Tool Configuration`에서 설정한 JDK의 이름을 써주면 된다.

```groovy
pipeline {
  tools {
    jdk("Adopt openJDK 11")
  }
}
```


# 참고

- [How to change the JDK for a Jenkins job?](https://stackoverflow.com/questions/28810477/how-to-change-the-jdk-for-a-jenkins-job/39608374)
