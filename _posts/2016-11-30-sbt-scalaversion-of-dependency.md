---
layout: post
title:  "[sbt] dependency 설정에 Scala 버전 자동으로 붙여주기"
date:   2016-11-30 00:17:00 +0900
categories: sbt dependency
tags: scala sbt dependency notation library version
---

Scala sbt에서 dependency를 추가할 때 아래처럼 써주면 된다.

```scala
libraryDependencies += "org.scala-tools" % "scala-stm_2.11.1" % "0.3"
```

그런데, 매번 scala 버전(여기서는 2.11.1)까지 써주기가 너무 불편하다. 또 scala 버전이 바뀌면 dependency 내용들을 다 바꿔줄 건가?? 이럴 때면 어김없이 등장하는 syntactic sugar!!

`groupID`와 `artifactID` 사이의 `%`를 `%%`로 써주면 scala 버전을 생략해도 자동으로 채워준다.

```scala
libraryDependencies += "org.scala-tools" %% "scala-stm" % "0.3"
```

하지만, 내가 사용해야 하는 scala 버전과 dependency의 버전이 다르다면 `%%`를 사용할 수 없다. T_T 그럴 때면 아쉽지만 아래처럼 별도 버전 기입을..

```scala
scalaVersion := "2.10.4"
libraryDependencies += "org.scala-tools" % "scala-stm_2.11.1" % "0.3"
```

# 참고
- [http://www.scala-sbt.org/0.13/docs/Library-Dependencies.html#Getting+the+right+Scala+version+with](http://www.scala-sbt.org/0.13/docs/Library-Dependencies.html#Getting+the+right+Scala+version+with)
