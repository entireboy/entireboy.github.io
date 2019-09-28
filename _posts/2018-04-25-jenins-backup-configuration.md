---
layout: post
title:  "[Jenkins] 설정 백업"
date:   2018-04-25 22:18:00 +0900
published: true
categories: [ jenkins ]
tags: [ jenkins, backup, config, configuration, plugin, quiet mode ]
---

젠킨스는 모든 설정을 파일로 저장하기 때문에, 서버의 디스크가 깨지면 문제가 심각하다. 디스크가 깨지기 전에 백업이 필요해서 [thinBackup 플러그인](https://plugins.jenkins.io/thinBackup)으로 주기적으로 백업을 해 보자.

젠킨스 설정은 모두 파일로 저장되기 때문에 `rsync`나 `scp` 등의 명령으로 간편히 백업을 해도 되지만, 젠킨스 서버가 여러개 있고 각 설정 위치가 다르다면 이것 또한 관리가 귀찮을 수 있다. ([Jenkins 잡 옮기기]({{ site.baseurl }}{% post_url 2017-11-02-jenkins-backup-restore %}) 참고) 일럴 때 이 플러그인을 사용하면 스스로 설정 위치를 찾아서 백업/복구를 하기 때문에 편하다.

> **NOTE**:  `quiet mode` 설정은 조심해야 해야 한다. 이 글 가장 아래에 설명


# 플러그인 설치/설정

플러그인은 젠킨스 설정 플러그인 화면에서 쉽게 검색해서 설치할 수 있다.

플러그인을 설치하면 젠킨스 설정 화면에서 아래와 같이 `ThinBackup`이 생긴걸 볼 수 있다.

{% include image.html file='/assets/img/2018-04-25-jenins-backup-configuration1.png' alt='Jenkins config' %}

지금 바로 매뉴얼하게 백업을 실행할 수 있고, 백업된 파일로 복구를 할 수도 있다.

{% include image.html file='/assets/img/2018-04-25-jenins-backup-configuration2.png' alt='ThinBackup config' %}

그리고 thinBackup 설정화면에서는 주기적으로 백업을 설정하거나, 풀백업/증분(?)백업 등의 여러 설정할 수 있다.

{% include image.html file='/assets/img/2018-04-25-jenins-backup-configuration3.png' alt='ThinBackup config' %}

```
**NOTE**
위의 설정 중 가장 조심할 설정은 `Force Jenkins to quiet mode after specified minutes`이다. 백업을 하기 위해 모든 job이 멈춰 있어야 하는데, 계속 job이 돌아가게 된다면 백업을 시작하고 이 시간 뒤에 자동으로 `quiet mode`로 빠지게 된다. `quiet mode`는 아무 job도 실행하지 않게 되는 말 그대로 조용해 지는 모드이다.

문제는, 위에 설정된 120분 보다도 긴 job이 있다면, 그 job이 끝나기를 기다리다가 `quiet mode`로 빠지면 아무 job도 실행되지 못 하는 상태가 된다. 예전에 job을 잘못 만든 탓도 있지만, 12시간이 넘어도 끝나지 않는 job을 기다리다가 Jenkins가 죽은 것과 같은 효과를 내기도 했었다.

- 2019. 09. 28. 추가
```

```
**NOTE**
주기적으로 백업을 하기 위해 스케쥴을 걸기 위해서는 플러그인 설치 후 젠킨스 재시작이 꼭 필요하다.
```


# 참고

- [ThinBackup - Jenkins plugin](https://plugins.jenkins.io/thinBackup)
- [Jenkins 잡 옮기기]({{ site.baseurl }}{% post_url 2017-11-02-jenkins-backup-restore %})
