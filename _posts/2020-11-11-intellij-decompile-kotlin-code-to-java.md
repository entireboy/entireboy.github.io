---
layout: post
title:  "[IntelliJ] Kotlin 코드를 Java로 확인하기"
date:   2020-11-11 22:18:00 +0900
published: true
categories: [ intellij ]
tags: [ intellij, kotlin, java, code, decompile, convert ]
---

Java에 익숙한 개발자가 Kotlin 코드를 작성하다 보면, 이 Kotlin 코드가 Java 코드로는 어떤 형태로 생성될지 궁금할 때가 있다.

Kotlin property 로 선언하면 Java 에서 어떻게 만들어지는지 등등

이럴 때 IntelliJ 에서 Kotlin 코드를 Kotlin Bytecode로 변환하고, 그 코드를 다시 Java로 decompile 하는 방식으로 비슷한 Java 코드를 확인할 수 있다.

`Shift`를 2번 누르거나 `Cmd + Shift + A`를 누르면 `Action`을 검색할 수 있다. 이 화면에서 `Kotlin Bytecode`를 검색해서 선택하면 스크린샷의 오른쪽과 같은 Kotlin Bytecode를 볼 수 있다. 위에 있는 `Decompile` 버튼을 누느면 이 바이트코드를 다시 Java로 변환해 준다. 사람이 읽기에는 좀 불편해 보일 수 있지만, 필요한 모든 정보는 다 보이는 것 같다.

{% include image.html file='/assets/img/2020-11-11-intellij-decompile-kotlin-code-to-java.png' alt='Convert Kotlin code to Java code' %}
