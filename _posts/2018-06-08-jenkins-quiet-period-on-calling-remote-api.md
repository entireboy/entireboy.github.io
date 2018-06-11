---
layout: post
title:  "[Jenkins] Remote API로 빌드 시 바로 실행되지 않고 pending되는 현상"
date:   2018-06-08 23:18:00 +0900
published: true
categories: [ jenkins ]
tags: [ jenkins, remote, api, remote api, rest, job, build, pending, quiet period ]
---

[Jenkins](https://jenkins.io/) 빌드를 REST API 등으로 시작할 수 있다. Jenkins 매뉴얼(?) [Remote access API](https://wiki.jenkins.io/display/JENKINS/Remote+access+API)을 보면 자세히 설명이 되어 있다. 간단히 아래처럼 job의 `/build` API를 호출하면 된다.

```bash
$ curl -i -X POST "http://{JENKINS_SERVER}/job/{JOB_NAME}/build" --user '{USERNAME}'
```

그런데, 간혹 아래 스냅샷처럼 job이 바로 실행되지 않고, pending되는 경우가 있다. `pending - in the quiet period. Expires in 59 min` 이 젠킨스의 경우는 API를 호출하고 60분을 기다려서 quiet period가 expire되면 그 후에 잡이 실행됐다. 또 다른 젠킨스의 경우는 몇 초 내에 바로 실행이 되기도 했다.

{% include image.html file='/assets/img/2018-06-08-jenkins-quiet-period-on-calling-remote-api1.png' alt='pending' %}

한참을 찾아보니 remote API 호출을 하면 Job은 quiet period에 들어간 것이고, 이 period 사이에 호출되는 동일한 job의 build는 한 번만 호출되게 하는 것이다. 실행 시간이 긴 job의 경우는 A가 호출해서 돌고 있는 사이에 B가 호출하면 B는 A가 끝날 때 까지 기다려야 하기 때문에 비효율적일 수 있어서, quiet period 동안 들어오는 호출을 모아서 한번에 A와 B가 실행될 수 있도록 하는 것이라고 한다. [Quiet Period Feature - Jenkins Blog](https://jenkins.io/blog/2010/08/11/quiet-period-feature/)


# 해결방법

`/build` API를 호출할 때 `delay` parameter를 주면, quiet period 시간을 조절할 수 있다.

```bash
$ curl -i -X POST "http://{JENKINS_SERVER}/job/{JOB_NAME}/build?delay=5sec" --user '{USERNAME}'
```

이 quiet period는 `젠킨스 관리` > `시스템 설정` 화면에서, 아래와 같이 시간 조절이 가능하다.

{% include image.html file='/assets/img/2018-06-08-jenkins-quiet-period-on-calling-remote-api2.png' alt='quiet period config' %}

하지만, 이건 젠킨스 UI에서 빌드할 때만 먹히는 것 같고, API 호출할 때는 안 먹히는 것 같다. 동일한 설정의 젠킨스 2대에서 API호출을 테스트했는데, 하나는 바로 실행되지만 다른건 60분을 quiet period로 대기했다. UI화면에서 빌드를 실행해도 내부적으로는 API를 호출한다고 하는데, 그 때 delay를 위에서 설정한 값으로 호출하는 것일 수도 있겠다. (확실하지는 않음 ㅋㅋ)


# 팁

덤으로.. parameter가 있는 job은 아래처럼 parameter의 key, value 쌍을 `--form` 으로 주면 된다. json 형태의 예제이고, xml 형태도 가능하다.

```bash
$ curl -X POST http://{JENKINS_SERVER}/job/{JOB_NAME}/build --form json='{"parameter":[{"name":"APP_NAME", "value":"my-app"}, {"name":"MODULE_NAME", "value":"my-module"}, {"name":"PHASE", "value":"alpha"}, {"name":"BRANCH_NAME", "value":"alpha"}]}' --user '{USERNAME}'
```


# 참고

- [How can I schedule a job to run in the future, but only one time ever - stack overflow](https://stackoverflow.com/questions/35029486/how-can-i-schedule-a-job-to-run-in-the-future-but-only-one-time-ever/39858002)
- [Remote access API](https://wiki.jenkins.io/display/JENKINS/Remote+access+API)
- [Quiet Period Feature - Jenkins Blog](https://jenkins.io/blog/2010/08/11/quiet-period-feature/)
