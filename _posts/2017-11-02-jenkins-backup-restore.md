---
layout: post
title:  "[Jenkins] 잡 옮기기/복사하기 (backup/restore/move)"
date:   2017-11-02 21:18:00 +0900
published: true
categories: [ jenkins ]
tags: [ jenkins, job, backup, restore, move, rename, archive, admin ]
---

[젠킨스](https://jenkins.io/)를 사용하다 보면, 아주 가끔 서버 이전을 해야 한다거나 새로 세팅하는 등 젠킨스를 옮겨야 하는 경우가 있다. 계속 운영 중인 잡(job)이 있기 때문에 Credential 이나 Fingerprint, Plugin 등 기존 제킨스 설정을 그대로 함께 옮겨야 한다면..??

젠킨스는 DB 같은 별도의 스토리지를 사용하지 않고, 폴더와 파일을 사용한다. 잡 이름으로 폴더를 생성하고, 그 안에 잡 설정 파일과 실행 히스토리 등을 별도로 저장한다. 때문에, 이 파일들만 잘 복사하면 젠킨스는 그대로 복사가 된다.


# 젠킨스 이동/복사

젠킨스를 복사해서 동일하게 띄우는 방법은 아주 간단하다.

1. 새 젠킨스를 설치한다. (이왕 새로 설정하는거 버전업을 이 때 해도 좋다.)
2. `JENKINS_HOME` 하위에 있는 파일들을 새 젠킨스로 복사한다. (보통 `JENKINS_HOME`은 `~/.jenkins` 이다.)
3. 새 젠킨스를 실행한다.

잡을 실행하고 이력(history/log)을 저장하는 workspace 경로를 다른 경로로 설정할 수 있기 때문에, 이 경로도 함께 복사해 주면 이전에 실행한 이력도 함께 복사할 수 있다. `JENKINS_HOME/jenkins.xml` 파일의 `<workspaceDir>`를 확인해 보자.


# 동일한 설정으로 Job 복사 (copy/move)

`JENKINS_HOME/jobs` 아래에 폴더 단위로 구성되어 있기 때문에 동일한 설정으로 잡을 복사하는게 쉽다.

1. 잡 폴더를 복사한다. (당연히 동일한 이름은 불가능하기 때문에 복사할 새 이름으로 바꿔야 한다.)
2. `젠킨스 설정`에서 > `Reload Configuration from Disk`를 한다.

역시 동일하게 잡 이름을 변경할 때도 폴더명만 rename 해주고 로딩하면 된다.


# Job 보관 (archive)

사용하던 잡을 임시로 제거하고, 나중을 위해 임시로 보관해 둘 때는 잡 폴더를 압축해 두고 폴더는 삭제하면 된다.

```bash
$ cd $JENKINS_HOME/jobs
$ tar czf my.job.tgz my.job
```

젠킨스 설정에서 리로드 잊지 말자. 귀찮으면, [Shelve Project Plugin](https://wiki.jenkins.io/display/JENKINS/Shelve+Project+Plugin)을 사용해 보자. 편하다. +_+


# 참고

- [https://wiki.jenkins.io/display/JENKINS/Administering+Jenkins](Administering Jenkins - Jenkins WIKI)
