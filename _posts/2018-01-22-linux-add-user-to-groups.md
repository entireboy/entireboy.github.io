---
layout: post
title:  "[Linux] 사용자에 그룹 추가"
date:   2018-01-22 23:18:00 +0900
published: true
categories: [ linux ]
tags: [ linux, unix, system, user, add, group, management ]
---

Linux system에서 사용자(user)에 그룹(group)을 추가할 때는 `usermod`명령을 쓰면 된다. user와 group을 관리하는 것이기 때문에 root 권한이 필요하다.

user의 group은 크게 primary와 secondary로 나뉘어 진다. primary group은 해당 user의 정해진 대표(?) group이다. 기본적으로 username과 동일한 이름이다. secondary group(supplementary group)을 user에게 추가해 주면 해당 group이 생성한 파일이나 프로세스 등을 접근할 수 있게 된다.

user에게 secondary group을 추가하는 방법을 알아보자. 먼저 예제에서 사용할 group `foo`와 `bar`를 추가한다.

```bash
$ # 사용할 group 생성
$ sudo groupadd foo
$ sudo groupadd bar
$ # /etc/group 파일을 열면, 생성한 group이 추가되어 있는 것을 볼 수 있다.
$ cat /etc/group
root:x:0:
...
foo:x:10000:
bar:x:10001:
$ # group 제거는 groupdel 명령이다.
$ # sudo groupdel foo
```

group `foo`와 `bar`를 uer `leocat`에 추가해 보자.

```bash
$ # id 명령으로 현재 설정된 id 정보나 group 정보를 알 수 있다.
$ sudo id leocat
uid=1000(leocat) gid=1000(leocat) groups=1000(leocat)
$ # -G 옵션으로 secondary group으로 foo 설정
$ sudo usermod -G foo leocat
$ sudo id leocat
uid=1000(leocat) gid=1000(leocat) groups=1000(leocat),10000(foo)
$ # -G 옵션만 주면 secondary group이 덮어써진다. foo -> bar
$ sudo usermod -G bar leocat
$ sudo id leocat
uid=1000(leocat) gid=1000(leocat) groups=1000(leocat),10001(bar)
$ # -a 옵션으로 secondary group을 추가할 수 있다. (append)
$ sudo usermod -G foo -a leocat
$ sudo id leocat
uid=1000(leocat) gid=1000(leocat) groups=1000(leocat),10000(foo),10001(bar)

$ # 콤마(,)를 이용해서 여러 group을 지정할 수 있다.
$ sudo usermod -G leocat leocat
$ sudo id leocat
uid=1000(leocat) gid=1000(leocat) groups=1000(leocat)
$ sudo usermod -G foo,bar leocat
$ sudo id leocat
uid=1000(leocat) gid=1000(leocat) groups=1000(leocat),10000(foo),10001(bar)

$ # group을 제거하면 user에 추가되어 있던 group도 제거된다.
$ sudo groupdel bar
[leocat@my-test ~]$ sudo id leocat
uid=1000(leocat) gid=1000(leocat) groups=1000(leocat),10000(foo)
```


# 참고

- [Linux: Add User to Group (Primary/Secondary/New/Existing)](http://www.hostingadvice.com/how-to/linux-add-user-to-group/)
- [Users and groups - archlinux](https://wiki.archlinux.org/index.php/users_and_groups)
