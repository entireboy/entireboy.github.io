---
layout: post
title:  "[Jenkins] job 실행 결과로 생성된 파일 저장하기"
date:   2022-06-13 22:18:00 +0900
published: true
categories: [ jenkins ]
tags: [ jenkins, archive, artifact, save, file, store, download, result ]
---

# 문제

Jenkins job을 돌려서 csv 파일을 생성하고, 그 파일을 다운로드 받고 싶을 때


# Archive the Artifacts

Job 실행 후 생성된 파일들을 artifact로 모아서 저장하는 기능이다.

{% include image.html file='/assets/img/2022/2022-06-13-jenkins-archive-the-artifacts-save-the-result1.png' alt='Archive the artifacts config' %}

Jenkins job 설정의 `빌드 후 조치(Post-build Actions)`에서 `Archive the artifacts`를 추가하고, 저장할 파일 경로와 이름을 설정한다.

Job을 실행하면, 각 build 마다 아래와 같이 저장된 파일을 확인할 수 있고 다운로드 받을 수 있다. `view` 버튼을 통해 웹 브라우저로 바로 확인도 가능하다.

{% include image.html file='/assets/img/2022/2022-06-13-jenkins-archive-the-artifacts-save-the-result2.png' alt='The archived artifacts' %}



# 참고
- [How to download build output files from jenkins UI console itself](https://stackoverflow.com/questions/41974070/how-to-download-build-output-files-from-jenkins-ui-console-itself)
