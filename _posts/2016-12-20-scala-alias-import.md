---
layout: post
title:  "[Scala] rename/alias import"
date:   2016-12-20 10:05:00 +0900
published: true
categories: [ scala, import ]
tags: [ scala, rename, alias, import, tip ]
---

Scala에는 동일한 클래스를 import할 수 없는 Java의 단점을 보완하고, 긴 클래스명을 내가 원하는 이름으로 바꿀 수 있는 기능이 있다.

```scala
import kr.leocat.test.{MyMeaninglesslyVeryLongClass => MyClass}
val mine = new MyClass
```

이제, 겁나 긴 클래스명인 `MyMeaninglesslyVeryLongClass`를 짧게 `MyClass`로 쓸 수 있다.

Scala에는 `Map`, `List` 등의 클래스가 이미 만들어져 있다. 여기서 `java.util.Map`과 `java.util.List`를 쓰려면 클래스명이 충돌나지 않도록 이름을 바꿔줘야 한다.

```scala
import java.util.{Map => JMap, List => JList}
```

참, 쉽죠잉-??

# 참고
- [http://alvinalexander.com/scala/how-to-rename-members-import-scala-classes-methods-functions](http://alvinalexander.com/scala/how-to-rename-members-import-scala-classes-methods-functions
)
- [http://blog.bruchez.name/2012/06/scala-tip-import-renames.html](http://blog.bruchez.name/2012/06/scala-tip-import-renames.html)
