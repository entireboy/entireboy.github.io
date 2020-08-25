---
layout: post
title:  "[Java] thread dump / heap dump 분석 툴"
date:   2016-03-07 01:27:36 +0900
published: true
categories: [ java, analyze ]
tags: [ java, thread dump, heap dump, analyze, tool, dump ]
---

하도 까먹어서 정리 중 =_= 매번 쓰면서도 이름이 기억 안 남;;

# 0. fastThread

  (2020. 08. 25. 추가)
  <https://fastthread.io/>

  이 사이트는 덤프 파일을 업로드 하면 분석해 준다. 역시 설치형은 미리 설치되어 있지 않으면 만사가 귀찮으니 그럴 때는..

# 1. ThreaLogic

  <https://java.net/projects/threadlogic> -> <https://github.com/sparameswaran/threadlogic>

  deahlock 같은걸 쉽게 찾을 수 있어서 개인적으로 가장 좋아하는 툴인데, Oracle 사이트가 개편되면서 프로젝트 페이지가 사라졌다. 그리고, 많은 사람들이 ThreadLogic을 찾는 글들이 많이 있었다. 그리고 그 중 글 하나([Where did ThreadLogic end up?](https://community.oracle.com/thread/4052392))에서 본인이 개발자인데 Oracle을 퇴사하고 관리를 못 했다는 댓글을 따라가 보면 <https://github.com/sparameswaran/threadlogic>로 안내한다. 주인 찾았으니 앞으로 계속 좋아지길.. (아마도 사실이겠지 하며 링크 교체)

  백업 파일 뒤적이다 우연히 오래 전에 받아둔 ThreadLogic 발견. [1.1.205 버전]({{ site.baseurl }}/assets/file/tool/ThreadLogic-1.1.205.jar)

# 2. VisualVM

  <https://visualvm.java.net/>

  별도로 다운 받아서 설치해도 되고, JDK에 기본으로 들어 있으니 그냥 써도 된다. 아래 위치에 있다. JMX 등으로 remote 서버에 붙을 수도 있는 다양한 기능을 가진 툴

  ```
  ${JAVA_HOME}/bin/jvisualvm
  ```

# 3. IBM HeapAnalyzer

  <https://www.ibm.com/developerworks/community/groups/service/html/communityview?communityUuid=4544bafe-c7a2-455f-9d43-eb866ea60091>
