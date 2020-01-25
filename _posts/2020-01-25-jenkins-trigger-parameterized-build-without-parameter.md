---
layout: post
title:  "[Jenkins] 전달인자 없을 때 Parameterized Trigger 플러그인이 동작하지 않는 문제"
date:   2020-01-25 23:18:00 +0900
published: true
categories: [ jenkins ]
tags: [ jenkins, trigger, job, build, parameter, parameterized trigger, trigger, plugin ]
---

Jenkins [Parameterized Trigger Plugin](https://plugins.jenkins.io/parameterized-trigger)을 사용하면 job을 실행한 다음 또 다른 job을 실행할 수 있다. 그리고 job을 실행할 때 전달인자(parameter)도 함께 줄 수 있다. 다음에 실행되는 job이 downstream 으로 등록된다.

> 2017년이 마지막 릴리즈였는데, 최근(2019년 12월)에 새버전([2.36](https://github.com/jenkinsci/parameterized-trigger-plugin/releases))이 나왔다.

# 문제

Parameterized Trigger 설정을 했지만, 다음 job이 동작하지 않는 문제가 있었다.


# 해결방법

너무 멍청하게도 글씨를 제대로 못 읽어서, 실행되지 못 한 것이다.
downstream을 설정해 줄 때, downstream으로 전달해 주는 파라미터가 없다면 `Trigger build without parameters`를 체크해 주어야 한다.

{% include image.html file='/assets/img/2020-01-25-jenkins-trigger-parameterized-build-without-parameter.png' alt='Trigger parameterized build 설정' %}

아무 파라미터도 지정하지 않았다면 자동으로 체크된 것처럼 동작해도 좋으련만..
