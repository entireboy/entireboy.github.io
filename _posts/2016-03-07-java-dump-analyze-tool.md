---
layout: post
title:  "[Java] thread dump / heap dump 분석 툴"
date:   2016-03-07 01:27:36 +0900
categories: [ java, analyze ]
tags: [ java, thread dump, heap dump, analyze, tool ]
---

하도 까먹어서 정리 중 =_= 매번 쓰면서도 이름이 기억 안 남;;

1. [ThreaLogic](https://java.net/projects/threadlogic)

2. [VisualVM](https://visualvm.java.net/) - 별도로 다운 받아서 설치해도 되고, JDK에 기본으로 들어 있으니 그냥 써도 된다. 아래 위치에 있다. JMX 등으로 remote 서버에 붙을 수도 있는 다양한 기능을 가진 툴
```
${JAVA_HOME}/bin/jvisualvm
```

3. [IBM HeapAnalyzer](https://www.ibm.com/developerworks/community/groups/service/html/communityview?communityUuid=4544bafe-c7a2-455f-9d43-eb866ea60091)
