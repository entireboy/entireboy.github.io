---
layout: post
title:  "[Jenkins] Docker로 띄운 Jenkins의 초기 비밀번호"
date:   2020-02-16 22:18:00 +0900
published: true
categories: [ jenkins ]
tags: [ jenkins, docker, init, admin, password, location ]
---

Jenkins를 처음 설치하면 아래 경로에 비밀번호 파일이 생성되고, 처음 접속 시에 사용해야 한다.

```bash
/var/jenkins_home/secrets/initialAdminPassword
```

[[ 사진 ]]
{% include image.html file='/assets/img/2020-02-16-jenkins-initial-admin-password-on-jenkins-docker-image1.png' alt='Require init admin password' %}


그런데 docker 이미지로 띄우면 volume을 설정해도 안 보인다. (못 찾는건가 =_=)


# 로그 확인

이럴 때는 [Jenkins 문서](https://jenkins.io/doc/book/installing/#setup-wizard)에 있능 것처럼 docker 로그를 잘 보면 Jenkins가 뜰 때 남긴 비밀번호가 있으니 눈을 부릅 뜨고 찾아 보자.

```bash
jenkins260   | Feb 16, 2020 12:36:17 PM jenkins.install.SetupWizard init
jenkins260   | INFO:
jenkins260   |
jenkins260   | *************************************************************
jenkins260   | *************************************************************
jenkins260   | *************************************************************
jenkins260   |
jenkins260   | Jenkins initial setup is required. An admin user has been created and a password generated.
jenkins260   | Please use the following password to proceed to installation:
jenkins260   |
jenkins260   | 1ecff1ec84cd448c9b1093a706ce6ac4
jenkins260   |
jenkins260   | This may also be found at: /var/jenkins_home/secrets/initialAdminPassword
jenkins260   |
jenkins260   | *************************************************************
jenkins260   | *************************************************************
jenkins260   | *************************************************************
jenkins260   |
```

{% include image.html file='/assets/img/2020-02-16-jenkins-initial-admin-password-on-jenkins-docker-image2.png' alt='Initial admin password on log' %}


# 컨테이너 내 파일 확인

Docker 컨테이너에 직접 접속해서 파일을 확인할 수도 있다.

```bash
$ docker exec <CONTAINER_NAME> cat /var/jenkins_home/secrets/initialAdminPassword
1ecff1ec84cd448c9b1093a706ce6ac4
```


# 참고

- [Unlocking Jenkins - Jenkins Handbook](https://jenkins.io/doc/book/installing/#setup-wizard)
