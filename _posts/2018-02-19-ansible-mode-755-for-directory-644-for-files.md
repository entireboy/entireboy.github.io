---
layout: post
title:  "[Ansible] 폴더는 755, 파일은 644로 mode 설정하기 (chmod)"
date:   2018-02-11 22:18:00 +0900
published: true
categories: [ ansible ]
tags: [ ansible, file, directory, chmod, mode, execute, search ]
---

[Ansible](https://www.ansible.com/)에서 폴더와 파일 권한을 설정할 때 [file module](http://docs.ansible.com/ansible/latest/file_module.html)을 쓰면 된다.

```yaml
- name: "Mode 755 for directory"
  file:
    path: "/home/leocat/tmp"
    mode: 0755
```

그런데, 폴더 `mode`는 `755`를 주고, 파일은 `644`를 주고 싶다면?? 사실 ansible로 깔끔히 할 수 있는 방법이 없다.

`file module`의 `mode`는 `/usr/bin/chmod` 설정을 그대로 사용하기 때문에 그 방법을 이용하자. `mode`의 소문자 `x`는 폴더는 검색(리스팅) 권한을 뜻하고 파일의 경우는 실행 권한을 뜻한다. 하지만, 대문자 `X`는 폴더인 경우는 소문자 `x`와 동일하지만, 파일인 경우는 다른 사용자에게 이미 실행 권한이 있을 때만 실행 권한을 주게 된다.

> execute (or search for directories) (x), execute/search only if the file is a directory or already has execute permission for some user (X) -- from [chmod man page](https://linux.die.net/man/1/chmod)

```yaml
- name: "Mode 755 for directory and 644 for files"
  file:
    path: "/home/leocat/tmp"
    mode: u=rwX,g=rX,o=rX
    recurse: yes
```

위처럼 `recurse`를 주고 실행하면 `/home/leocat/tmp`는 폴더이기 때문에 실행 권한이 추가된다. 그리고 `hello.sh`처럼 이미 실행 권한이 있는 경우는 그걸 따라서 권한이 추가되고, `hello.txt`처럼 실행 권한이 하나도 없는 경우는 그걸 따라서 권한이 없게 된다.

```bash
$ ls -l /home/leocat
합계 0
dr-x------ 2 deploy deploy 37  2월 20 13:41 tmp
$ ls -l /home/leocat/tmp
합계 4
-rwx------ 1 deploy deploy 13  2월 20 13:11 hello.sh
-rw------- 1 deploy deploy  0  2월 20 13:41 hello.txt

$ ansible ... # ansible 스크립트 실행 시켜주면..

$ ls -l /home/leocat
합계 0
drwxr-xr-x 2 deploy deploy 37  2월 20 13:41 tmp
$ ls -l /home/leocat/tmp
합계 4
-rwxr-xr-x 1 deploy deploy 13  2월 20 13:11 hello.sh
-rw-r--r-- 1 deploy deploy  0  2월 20 13:41 hello.txt
```


# 참고

- [Ansible - Mode 755 for directories and 644 for files recursively](https://stackoverflow.com/questions/28778738/ansible-mode-755-for-directories-and-644-for-files-recursively)
- [chmod man page](https://linux.die.net/man/1/chmod)
