---
layout: post
title:  "[ktlint] 빌드 실패 - finished with non-zero exit value"
date:   2020-11-19 22:18:00 +0900
published: true
categories: [ kotlin ]
tags: [ ktlint, kotlin, lint, format, build, fail ]
---

프로젝트에 처음 ktlint 를 적용하면 이런 당혹스러운 에러를 만나고 빌드 실패 화면을 보게 된다.

```bash
$ ./gradlew clean ktlintCheck
$ #   또는
$ # ./gradlew clean check

* What went wrong:
Execution failed for task ':my-service:ktlintIntegration-testSourceSetCheck'.
> A failure occurred while executing org.jlleitschuh.gradle.ktlint.KtLintWorkAction
   > Process 'command '/Users/me/.jabba/jdk/adopt@1.8.0-232/Contents/Home/bin/java'' finished with non-zero exit value 1

* Try:
Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output. Run with --scan to get full insights.

* Get more help at https://help.gradle.org

BUILD FAILED in 4s
20 actionable tasks: 1 executed, 19 up-to-date
```

{% include image.html file='/assets/img/2020-11-19-ktlint-build-failed-with-non-zero-exit-message1.png' alt='Build failed' %}

한참을 찾았지만 원인이나 이유를 찾기란 쉽지 않았다. 비슷한 증상을 겪는 글도 발견하기 어려웠다.

체념하고 문제로 지적된 내용들을 고치기 시작했는데..

그런데 왠걸?? 빌드가 성공한다..??

{% include image.html file='/assets/img/2020-11-19-ktlint-build-failed-with-non-zero-exit-message2.png' alt='Build successful' %}

속이 다 후련하네!!


# 해결방법

lint가 지적한 사항들을 수정하면 빌드가 성공한다 @_@

좀 친절하게 메시지를 주면 어디 덧나나;;
