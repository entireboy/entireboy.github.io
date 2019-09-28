---
layout: post
title:  "[Jenkins] 잡 실행 시 일정 시간 지연시간(delay) 주기 - quiet period"
date:   2019-09-27 23:18:00 +0900
published: true
categories: [ jenkins ]
tags: [ jenkins, feature, delay, quiet period, run, job ]
---

Jenkins job을 실행할 때 일정 시간의 지연시간(delay)를 주고 싶을 때가 있다. job을 실행시키면 바로 실행되는 것이 아니라, 다른 job을 대기하는 것 처럼 `대기 (pending)` 상태로 기다리게 된다.

{% include image.html file='/assets/img/2019-09-27-jenkins-quiet-period-feature1.png' alt='pending job in quiet period' width='300px' %}

단, 이 설정은 버튼을 클릭하거나 Remote API를 통해 호출하는 경우이다. 스케줄을 걸어서 job을 실행시키는 경우에는 `quiet period`가 적용되지 않는다.


# 모든 job에 설정

이런 기능은 (멀리 떨어진) 여러 사람이 동시에 같은 job을 실행시킬 수도 있기 때문에, 일정 시간을 대기하면서 모아서 실행시키기 위함이 있다. 그래서 일반적으로 Jenkins 전체 설정에 default로 적용되어 있다.

`Jenkins 관리 (Manage Jenkins)` > `시스템 설정 (Configure System)` 화면에서 `Quiet period`를 설정할 수 있다. (초 단위) 기본적으로 5초이고, 0초 이상의 값을 설정해야 한다.

{% include image.html file='/assets/img/2019-09-27-jenkins-quiet-period-feature2.png' alt='Config quiet period to all jobs' %}


# 특정 job에만 설정

Jenkins의 모든 job에 기본적으로 설정된 quiet period 대신 job 마다 설정할 수도 있다. job 설정에서 `General` > `고급 (Advanced)...`을 클릭하면 추가로 설정할 수 있는 항목들이 나타난다. 그 안에 `Quiet period`를 설정해 주면 된다. (초 단위)

{% include image.html file='/assets/img/2019-09-27-jenkins-quiet-period-feature3.png' alt='Config quiet period to specific jobs 1' %}

{% include image.html file='/assets/img/2019-09-27-jenkins-quiet-period-feature4.png' alt='Config quiet period to specific jobs 2' %}


# Remote API로 실행할 때 마다 설정

Jenkins의 기본 설정도, job의 설정도 무시하고 **매번 호출할 때 마다** `quiet period`를 설정할 수 있다. `/build` URL을 호출해서 Remote API를 실행할 때 `delay` 파라미터를 주면 된다.

```bash
$ curl -i -X POST \
"http://{JENKINS_SERVER}/job/{JOB_NAME}/build?delay=5sec" \
--user '{USERNAME}'
```


# 참고

- [Quiet Period Feature - Jenkins Blog](https://jenkins.io/blog/2010/08/11/quiet-period-feature/)
