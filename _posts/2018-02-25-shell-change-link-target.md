---
layout: post
title:  "[Shell] link 링크 변경"
date:   2018-02-25 21:18:00 +0900
published: true
categories: [ shell ]
tags: [ shell, bash, change, target, symbolic, link ]
---

symbolic link를 생성한 후 링크는 그대로 두고 가리키는 파일을 변경하고 싶은 경우가 있다. 몇 가지 방법이 있다.:

1. 링크를 삭제하고 새로 만든다.
2. 링크가 가리키는 파일을 다른 파일로 변경한다.

여기서는 기존 링크를 새 파일로 덮어쓰는 방법을 소개한다. 지우고 만드는건 왠지 일을 두 번 해야 하니깐 귀찮아서;;

링크를 다른 경로로 변경시키려면 `-f` 옵션과 함께 `-n` 옵션을 주면 된다. `ln` 명령 버전 등에 따라 `-n` 옵션이 필요 없는 경우도 있다.

```bash
$ mkdir release_20171225
$ mkdir release_20171226
$ ln -s release_20171225 latest
$ ls -al
합계 0
lrwxrwxrwx 1 deploy deploy 16 12월 19 15:20 latest -> release_20171225
drwxr-xr-x 2 deploy deploy  6 12월 19 15:20 release_20171225
drwxr-xr-x 2 deploy deploy  6 12월 19 15:20 release_20171226
$ # 1226으로 덮어쓰고 싶지만, 1225 그대로이다.
$ ln -s release_20171226 latest
$ ls -al
합계 0
lrwxrwxrwx 1 deploy deploy 16 12월 19 15:20 latest -> release_20171225
drwxr-xr-x 2 deploy deploy 30 12월 19 15:20 release_20171225
drwxr-xr-x 2 deploy deploy  6 12월 19 15:20 release_20171226
$ # 야호!! -f -n 옵션 주니까 된다!!
$ ln -sfn release_20171226 latest
$ ls -al
합계 0
lrwxrwxrwx 1 deploy deploy 16 12월 19 15:20 latest -> release_20171226
drwxr-xr-x 2 deploy deploy 54 12월 19 15:20 release_20171225
drwxr-xr-x 2 deploy deploy  6 12월 19 15:20 release_20171226
$
```


# 참고

[How to delete or replace an already created symbolic link?](https://askubuntu.com/questions/13363/how-to-delete-or-replace-an-already-created-symbolic-link)
[how to change where a symlink points](https://unix.stackexchange.com/questions/151999/how-to-change-where-a-symlink-points)
