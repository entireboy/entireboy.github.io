---
layout: post
title:  "[macOS] macOS 업데이트 이후 git 등 커맨드라인 툴이 동작 안 할 때"
date:   2018-10-09 23:18:00 +0900
published: true
categories: [ macOS ]
tags: [ macOS, update, version up, upgrade, OS, command line tool, git, tool, developer tool, install, version control ]
---

macOS는 버전업 할 때 마다, 잘 쓰던 커맨드라인 개발자 툴이 사라진다. 아래처럼 `git`과 같은 개발자 도구가 없어져서 에러를 뱉는다.

```bash
$ git
xcrun: error: invalid active developer path (/Library/Developer/CommandLineTools),
missing xcrun at: /Library/Developer/CommandLineTools/usr/bin/xcrun
```

다음 명령으로 커맨드라인 개발자 툴을 설치해 준다. 혹은 xcode를 설치해도 된다.

```bash
$ xcode-select --install
```

1, 2년에 한 번 업데이트를 하니 매번 까먹어서 기록


# 참고

- [macOS Mojave: invalid active developer path](https://apple.stackexchange.com/questions/254380/macos-mojave-invalid-active-developer-path)
