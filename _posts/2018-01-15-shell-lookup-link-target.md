---
layout: post
title:  "[Shell] link 대상 확인"
date:   2018-01-15 21:18:00 +0900
published: true
categories: [ shell ]
tags: [ shell, bash, check, lookup, target, symbolic, link ]
---

링크가 걸린 원래 경로를 확인하고 싶다면 `readlink`명령을 사용하면 된다.

```bash
$ ls -al
lrwxrwxrwx 1 user user 24 12월 14 16:49 my_hello.sh -> /some/where/hello.sh
$ readlink my_hello.sh
/some/where/hello.sh
$
```


# 참고

- [How to verify the target of a symbolic link points toward a particular path](https://unix.stackexchange.com/questions/192294/how-to-verify-the-target-of-a-symbolic-link-points-toward-a-particular-path)
