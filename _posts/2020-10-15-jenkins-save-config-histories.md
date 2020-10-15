---
layout: post
title:  "[Jenkins] 변경된 설정을 이력으로 남기기 (config history)"
date:   2020-10-15 22:18:00 +0900
published: true
categories: [ jenkins ]
tags: [ jenkins, save, job, config, history, change, comment, log, backup, restore ]
---

Jenkins를 팀에서 여러 사람들이 쓰면 이력 관리가 어려운 점이 있다. 누가 무엇을 바꿨는지 공유가 어렵고 내가 바꿨어도 언제부터 적용된건지도 까먹는다. 1년 전쯤 설정을 이렇게 바꿨는데, 왜 그랬지는지 기억 안 나서 힘들다. T_T


# Tools

역시 이럴 때는 역시 도구의 도움을 받아야.. 플러그인 찾아보면 여러가지가 많네 역시 Jenkins 좋아.

- Jenkins Configuration as Code (JCasC): [https://www.jenkins.io/projects/jcasc/](https://www.jenkins.io/projects/jcasc/)
- Job Configuration History: [https://plugins.jenkins.io/jobConfigHistory/](https://plugins.jenkins.io/jobConfigHistory/)
- SCM Sync Configuration (deprecated): [https://plugins.jenkins.io/scm-sync-configuration/](https://plugins.jenkins.io/scm-sync-configuration/)


## JCasC

[Jenkins Configuration as Code(JCasC)]([https://www.jenkins.io/projects/jcasc/](https://www.jenkins.io/projects/jcasc/))는 Jenkins에서 공식적으로 진행하고 있는 프로젝트이다. Jenkins의 모든 설정을 코드로 관리하기 때문에, 설정을 바꾸면 바로 저장하고 변경 이력을 git 등으로 관리하기 좋다. 그리고 동일한 설정으로 Jenkins 서버를 여러대 복구하기 쉽다.

그런데, 아직은 설정하는 방법이 너무 어렵다. 모든 xml 설정과 job description을 일일이 손으로 만들어야 한다. 특히 job dsl을 활용해서 job description을 기술하는건 정말 토나올 정도.. UI로 만든 설정을 그대로 export 할 수만 있어도 쓰기 좋을 것 같다.

그리고 Jenkins의 모든 설정을 파일 하나로 다 기술해야 하기 때문에 job이 많은 경우 엄청난 라인의 파일을 보게 된다. 팀 CI Jenkins에는 40개 정도의 job 이 있는데, 이 yml 파일 1500라인이 넘었다. 파일이라도 나눌 수 있다면 좋을텐데!!

플러그인 관리 또한 힘들다. 코드로 Jenkins를 복구한 뒤에 플러그인을 관리하는 플러그인을 사용해서 다시 플러그인을 다운받고 플러그인 설정을 로딩해야 한다. 플러그인 리스트를 별도 파일로 관리하다보니, 플러그인 끼리 디팬던시가.. 휴.. 그리고 사용 중인 그 많은 플러그인을 다운받으려면 시간이 정말 오래 걸린다. 이 시간에 플러그인 경로를 주기적으로 백업해 두고 복사해서 복구하는게 어떨지 =_= (사실 이 내용은 이력 관리가 아닌 빠른 복구에 해당하는 내용이다.)

오늘의 결론. **JCasC는 아직은 사용하기 불편한 점이 많다. 앞으로 더 나아지기를 기다리며, 지금은 패스**


## Job Configuration History

[Job Configuration History]([https://plugins.jenkins.io/jobConfigHistory/](https://plugins.jenkins.io/jobConfigHistory/))는 job 설정을 변경할 때 마다 이력을 남겨주는 플러그인이다. Job 뿐만 아니라 **Jenkins의 모든 설정**을 이력으로 남길 수 있다!!

각 변경 마다 스냅샷을 저장하고, diff까지 볼 수 있다.

{% include image.html file='/assets/img/2020-10-15-jenkins-save-config-histories1.png' alt='Job config history diff' %}
{% include image.html file='/assets/img/2020-10-15-jenkins-save-config-histories2.png' alt='Job config history diff' %}

그리고 또 다른 장점은 설정을 수정할 때 마다 변경 이력을 함께 남길 수 있다.

{% include image.html file='/assets/img/2020-10-15-jenkins-save-config-histories3.png' alt='Save change comment' %}

변경 메시지를 남기면 아래처럼 `파란 i` 아이콘이 생기고, diff를 보면 메시지도 함께 확인할 수 있다.

{% include image.html file='/assets/img/2020-10-15-jenkins-save-config-histories4.png' alt='Check change comment' %}
{% include image.html file='/assets/img/2020-10-15-jenkins-save-config-histories5.png' alt='Check change comment' %}

Jenkins 시스템 설정에서 이력의 보관개수/보관기간 등을 지정하거나 보관 경로를 바꿀 수 있다.

{% include image.html file='/assets/img/2020-10-15-jenkins-save-config-histories6.png' alt='Config Job Config History Plugin' %}

단점은 이 플러그인 역시 다른 플러그인처럼 모든 내용을 master node 로컬($JENKINS_HOME/config-history)에 저장된다. 따라서 master node가 죽는 경우를 대비해서 백업을 잘 해둬야 한다.


## SCM Sync Configuration

[SCM Sync Configuration](https://plugins.jenkins.io/scm-sync-configuration/) 플러그인은 git과 같은 별도의 버전관리 시스템으로 설정 변경 이력을 남기도록 강제하는 플러그인이다. 설정을 변경할 때 마다 별도의 repository에 commit을 하게 된다.

문제는 deprecated 되었고, JCasC를 사용하도록 가이드 하고 있다.

> Deprecated:This plugin has been marked as deprecated. In general, this means that this plugin is either obsolete, no longer being developed, or may no longer work. See the documentation for further information about the cause for the deprecation, and suggestions on how to proceed. The deprecation process is also documented here.

{% include google-ad-content %}


# 백업과 빠른 복구

Jenkins를 쓸 때 가장 불안한 점은 master node가 죽거나 지워지면 모든 설정을 잃어버린다는 점이다. 항상 백업을 해두자. 그리고 빠르게 복구할 수 있도록 방법을 마련해 두자.

여담인데, 이전 회사에서 우연히 백업을 해뒀는데 내가 퇴사한 이후에 Jenkins 서버가 날아가는 사건이!! 뚜둔!! 그 때 옆 팀 팀장님이 너무 불안해 해서 백업 한 벌 떠두고, 매일 새벽에 백업되도록 해뒀는데.. 설마하던 그 사건이 벌어졌다. 우연히 백업해둔 그 파일로 복구를 했는데, 그 이후로 1-2년간 변경된 내용은 이미.. 매일 새벽에 하던 백업은 로컬에 해둬서 아마 복구가 안 된듯 싶은데, 다음부터는 리모트 스토리지로 백업해 둬야지!!


# 그리고 이력 관리

그리고 또 Jenkins를 쓰면서 불편한 점은 (나를 포함한) 누군가가 설정을 바꿨는데, 왜 바꿨는지 도무지 기억이 나지 않을 때이다.

빌드 로그를 70개 보관하기로 변경했는데, 왜 70개였는지 기억이 나지 않거나 그 사람이 팀을 떠나면?? (70개는 매일 한 번 실행되는 배치의 2달치 로그를 비교하면서 보기 위함이었다.) 분명 팀에 코드리뷰와 함께 공유도 했지만, 직접 작업한 나도 30개, 60개, 70개 설정 바꿀 때 마다 자꾸 헷갈리는데 다른 사람이라면 당연히 기억이 안 날거다.

이런 변경을 할 때 마다 이유를 함께 적어 두면 히스토리 파악에 좋다.


# 참고

- [Jenkins Configuration as Code(JCasC)]([https://www.jenkins.io/projects/jcasc/](https://www.jenkins.io/projects/jcasc/))
- [Job Configuration History]([https://plugins.jenkins.io/jobConfigHistory/](https://plugins.jenkins.io/jobConfigHistory/))
- [SCM Sync Configuration]([https://plugins.jenkins.io/scm-sync-configuration/](https://plugins.jenkins.io/scm-sync-configuration/))
