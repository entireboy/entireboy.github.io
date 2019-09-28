---
layout: post
title:  "[Jenkins] 성공할 때 까지 잡 반복시키기 - quiet period(delay)와 함께"
date:   2019-09-28 23:18:00 +0900
published: true
categories: [ jenkins ]
tags: [ jenkins, repeat, run, job, quiet period, delay, until, status, stable, unstable, build ]
---

# 문제

동일한 Jenkins job을 성공할 때 까지 반복시키고 싶다.

예를 들어, 다른 작업의 종료여부를 모니터링하는 job이 있는데, 특정 시간부터 다른 작업이 종료됐는지 5분 마다 한번씩 체크하고 종료하지 못 했다면 slack 등으로 메시지를 보낸다. 그리고 그 작업이 종료되면 이 모니터링 job도 함께 종료한다. 5분 마다 종료 됐는지 체크하는 메시지를 받을 수 있는 것이다.

- 동일한 job을 반복적으로 실행해야 한다.
  - 매일 정해진 시간이 되면 job이 실행된다.
  - 특정 조건이 되면 반복을 멈춘다.
- job 반복의 사이에는 일정 시간 delay가 있다.


# 해결방법

위의 문제는 간단한 Jenkins job 설정만으로 가능하다.


## 반복 사이의 delay (quiet period)

job을 실행시킬 때 바로 실행시키지 않고, 일정 시간의 delay를 주고 싶다면 [Quiet Period](https://jenkins.io/blog/2010/08/11/quiet-period-feature/)를 쓰면 된다. 다른 글([Quiet Period 설정]({{ site.baseurl }}{% post_url 2019-09-27-jenkins-quiet-period-feature %}))에서 정리한 것처럼 job에 `quiet period`를 설정해 주면 된다.

job 설정에서 `General` 영역에 있는 `고급 (Advanced)...`를 클릭하면 숨겨진 설정이 나온다.

{% include image.html file='/assets/img/2019-09-28-jenkins-repeat-job-until-success-with-quiet-period1.png' alt='Config quiet period to specific jobs 1' %}

여기에 `Quiet period`를 초 단위로 설정해 주면 된다.

{% include image.html file='/assets/img/2019-09-28-jenkins-repeat-job-until-success-with-quiet-period2.png' alt='Config quiet period to specific jobs 2' %}

`quiet period`가 설정된 job을 실행하면 설정한 시간 동안 `대기 (pending)` 상태로 빠져서 job 실행이 대기하게 된다.

{% include image.html file='/assets/img/2019-09-28-jenkins-repeat-job-until-success-with-quiet-period4.png' alt='Pending job in quiet period' width='300px' %}


## 동일한 job을 반복시키기

job 설정에서 `빌드 후 조치`에 `Trigger parameterized build on other projects`를 추가해서 다른 job을 실행시키면 된다. 여기서는 job을 계속 반복시킬 것이기 때문에 `Projects to build`로 job 이름을 그대로 적어주면 된다. job에 다른 전달인자를 바꾸고 싶다면 아래의 `ADD PARAMETERS`를 설정해 주면 된다.

{% include image.html file='/assets/img/2019-09-28-jenkins-repeat-job-until-success-with-quiet-period3.png' alt='Config trigger other project' %}

job 이름을 변경해도 자동으로 따라 바뀌니 걱정하지 않아도 된다.


## 특정 조건으로 job 반복 멈추기

`Trigger parameterized build on other projects` 설정으로 다른 job을 실행시킬 때 현재 build의 상태를 체크할 수 있다. 이 글의 맨 위에서 설명한 예시처럼 모니터링할 다른 작업이 종료되지 못 한 상태는 Jenkins job의 `unstable` 상태로 정의하고, 정상적으로 종료된 경우를 `stable`로 정의할 수도 있다.

정리를 하면, job이 정상상태(`stable`)로 돌아올 때 까지 job을 반복해서 실행시키는 것이다.

{% include image.html file='/assets/img/2019-09-28-jenkins-repeat-job-until-success-with-quiet-period3.png' alt='Config trigger other project' %}

위처럼 `Trigger when build is`를 `unstable`이거나 `fail`인 경우만 실행시키도록 설정한다. 모니터링이 끝나고 job이 stable 상태로 돌아오면 반복이 끝나게 된다.


# 정리

- `빌드 후 조치`의 `Trigger parameterized build on other projects` 설정으로 job이 `stable` 상태로 돌아올 때 까지 job을 반복 실행 시킨다.
- job 설정에서 `quiet period`로 다음 job을 실행시킬 때 일정시간 delay를 준다.

아래 스냅샷의 build #10과 #11은 `unstable` 상태로 계속 반복되어 실행된 상태이고, #12가 실행을 대기 중이다.

{% include image.html file='/assets/img/2019-09-28-jenkins-repeat-job-until-success-with-quiet-period4.png' alt='Pending job in quiet period' width='250px' %}

build #12가 정상적으로 실행이 끝나서 `stable` 상태가 되면 더 이상 job이 반복되지 않는다.

{% include image.html file='/assets/img/2019-09-28-jenkins-repeat-job-until-success-with-quiet-period5.png' alt='Finished repeating job' width='250px' %}



# 참고

- [Quiet Period Feature - Jenkins Blog](https://jenkins.io/blog/2010/08/11/quiet-period-feature/)
- [Quiet Period 설정]({{ site.baseurl }}{% post_url 2019-09-27-jenkins-quiet-period-feature %})
