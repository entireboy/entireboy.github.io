---
layout: post
title:  "[Java] jabba - java version manager"
date:   2020-08-15 21:18:00 +0900
published: true
categories: [ java ]
tags: [ java, jabba, version, manager, jdk, openjdk, intellij ]
---

여러 프로젝트를 개발하다 보면 여러 버전의 java 환경이 필요하게 된다. A 프로젝트는 java 8 이고, B 프로젝트는 java 11 이고..

[jabba](https://github.com/shyiko/jabba)를 사용하면 여러 JDK 버전을 설치할 수 있고, 원할 때 마다 골라서 사용할 수 있다.


# Installation

설치는 간단하게, 다음 명령을 실행하면 설치 스크립트를 받아서 설치하게 된다.

```bash
$ curl -sL https://github.com/shyiko/jabba/raw/master/install.sh | bash && . ~/.jabba/jabba.sh
```

그리고 환경설정에 다음 내용을 추가한다. `~/.bashrc`나 `~/.zshrc` 파일에 추가해 주면 된다.

```bash
export JABBA_HOME="$HOME/.jabba"
[ -s "$JABBA_HOME/jabba.sh" ] && source "$JABBA_HOME/jabba.sh"
``


# Usage

```bash
$ jabba ls-remote                     # 설치할 수 있는 버전 확인
$ jabba install adopt@1.11.0-7        # 설치
$ jabba uninstall adopt@1.11.0-7      # 제거
$ jabba ls                            # 설치된 버전 확인
adopt@1.11.0-7
adopt@1.8.0-252

$ jabba current                       # 현재 사용 중인 버전 확인
adopt@1.11.0-6
$ java -version
openjdk version "11.0.6" 2020-01-14
OpenJDK Runtime Environment AdoptOpenJDK (build 11.0.6+10)
OpenJDK 64-Bit Server VM AdoptOpenJDK (build 11.0.6+10, mixed mode)

$ jabba use adopt@1.8.0-242           # 사용할 버전 선택
$ java -version
openjdk version "1.8.0_242"
OpenJDK Runtime Environment (AdoptOpenJDK)(build 1.8.0_242-b08)
OpenJDK 64-Bit Server VM (AdoptOpenJDK)(build 25.242-b08, mixed mode)

$ jabba alias default adopt@1.11.0-6  # 기본으로 사용할 버전 지정
default -> /../../.jabba/jdk/adopt@1.11.0-6
$ cat ~/.jabba/default.alias
adopt@1.11.0-6
```


# Config JDK on intelliJ

설치된 JDK 버전을 intelliJ의 SDK로 등록해서 사용하려면 아래처럼 프로젝트 설정(`Cmd + ;` 또는 `Ctrl + ;`)창에서 추가하면 된다.

{% include image.html file='/assets/img/2020-08-15-java-jabba-java-version-manager1.png' alt='Add JDK' %}

JDK가 설치되는 jabba의 기본 경로는 `~/.jabba/jdk`이기 때문에 숨겨진 경로(`.`로 시작하는 경로/파일)이다. 그래서 `Cmd + Shift + G` (또는 `Ctrl + Shift + G`) 단축키로 이동할 경로를 직접 적어줘야 숨겨진 경로로 이동할 수 있다. (물론 숨겨진 파일을 다 보는 옵션을 켜도 된다.)

{% include image.html file='/assets/img/2020-08-15-java-jabba-java-version-manager2.png' alt='Add JDK' %}


# 참고

- [jabba](https://github.com/shyiko/jabba)
