---
layout: post
title:  "[Shell] 디렉토리/파일 개수 세기"
date:   2017-07-27 22:18:00 +0900
published: true
categories: [ shell ]
tags: [ shell, bash, script, count, file, folder, directory ]
---

> 이전 블로그에서 옮겨온 포스트 (일부 동작 안 하는 샘플 수정)

디렉토리 내의 파일의 개수가 세고 싶어졌다. 하지만 리눅스라 유닉스는 잼병이다. @ㅅ@ 역시 또 검색~~!! 조금 길긴 하지만 재미난 방법을 찾았다.

우선 바쁜 분들을 위해, 간단히 오뜨케하는지 방법부터 알아보자.

```bash
$ #현재 디렉토리 내에 있는 디렉토리 개수를 알고 싶으면..
$ ls -l | grep ^d | wc -l

$ #현재 디렉토리 내에 있는 파일의 개수를 알고 싶으면..
$ ls -l | grep ^- | wc -l

$ #정규식(Regular Expression)을 이용해서 보다 세밀하게 파일명을 필터링하고 싶으면..
$ ls -l | grep ^- | awk '{print $9}' | grep '[정규식]' | wc -l

$ #설치된 grep 명령어 종류에 따라 정규식을 사용할 때,
$ #grep에 -e 옵션을 주거나 egrep을 사용해야 할 수도 있고
$ #정규식에 따옴표를 주어야 할 수도 있다.
$ ls -l | grep -e ^- | awk '{print $9}' | grep -e '[정규식]' | wc -l
$ ls -l | egrep ^- | awk '{print $9}' | egrep '[정규식]' | wc -l
```

서브 디렉토리를 포함하려면 `ls -Rl`을 사용하면 되겠다.

자.. 저게 뭐라고 쓰인건지 해석을 해보자. 해석하다 보면 내가 필요에 따라 변경해서 재미나게 사용할 수 있을 것 같다. 우선.. `ls` 명령어 부분부터 보자.

```bash
$ ls -l | grep ^- | wc -l
```

누구나 알겠지만, `ls` 명령어는 현재 디렉토리의 파일이나 디렉토리를 보여준다. `-l` 옵션을 붙여주면 아래와 같이 디렉토리의 내용을 보여준다. 역시 누구나 알겠지만 매 줄의 시작 부분을 보고 해당 내용의 종류를 알 수 있다. `d`로 시작하면 디렉토리이고, `-`로 시작하면 일반 파일이며, `l`로 시작하면 simbolic link이며, 더는 찾아보자. @ㅅ@ 자세한 것은 `man ls`에게 물어보도록 ㅋㅋ (다시 말하지만 난 수업 시간에 배운 아주 간단한 것 이외에는 기억에 남은게 없다. TㅅT)

`-l`옵션을 주면 아래와 같이 리스트로 나온다. 맨 앞부분이 해당하는 아이템의 종류이다.

```bash
$ ls -l
합계 16
lrwxrwxrwx 1 root root 10 11월 21 16:34 README -> manual.txt
-rw-r--r-- 1 root root 7155 11월 21 16:50 manual.txt
drwxr-xr-x 2 root root 4096 11월 21 16:46 test1
drwxr-xr-x 2 root root 4096 11월 21 17:19 test2
```

여기서 하위 디렉토리(서브 디렉토리)의 파일이나 디렉토리도 함께 세고 싶다면 `ls` 명령의 옵션인 `-R` 옵션도 추가하여 `ls -Rl`로 사용하면 된다. 그러면 아래와 같이 각 디렉토리의 내용이 보이게 된다.

```bash
$ ls -Rl
.:
합계 16
lrwxrwxrwx 1 root root 10 11월 21 16:34 README -> manual.txt
-rw-r--r-- 1 root root 7155 11월 21 16:50 manual.txt
drwxr-xr-x 2 root root 4096 11월 21 16:46 test1
drwxr-xr-x 2 root root 4096 11월 21 17:19 test2

./test1:
합계 12
-rw-r--r-- 1 root root 237 11월 21 16:46 BlackSheepWall
-rw-r--r-- 1 root root 54 11월 21 16:45 OperationCWAL
-rw-r--r-- 1 root root 182 11월 21 16:44 ShowMeTheMoney

./test2:
합계 8
-rw-r--r-- 1 root root 2224 11월 21 16:28 test.jpg
-rw-r--r-- 1 root root 53 11월 21 16:28 test.txt
```

{% include google-ad-content %}

그리고 다음 `grep` 명령어 부분을 보자. `grep`은 내가 원하는 패턴을 가지고 있는 라인만을 골라서 볼 수 있다. `grep`의 패턴은 정규식(Regular Expression)으로 설정한다.

```bash
$ ls -l | grep ^- | wc -l

$ #설치된 grep 명령어 종류에 따라 정규식을 사용할 때,
$ #grep에 -e 옵션을 주거나 egrep을 사용해야 할 수도 있다.
$ ls -l | grep -e ^- | wc -l
$ ls -l | egrep ^- | wc -l
```

여기서는 `grep`의 패턴으로 `^-`를 줬다. 정규식에서 `^`는 줄의 가장 첫부분을 나타낸다. `$`는 줄의 가장 마지막을 나타낸다. 따라서 `^-`는 줄의 맨 앞이 `-`로 시작하는 것을 찾는 것이다. `ls -l` 명령어의 결과로 일반 파일은 `-`로 시작하니 파일의 개수를 세기 위해 `^-`를 패턴으로 사용한 것이다. 만일 디렉토리의 개수를 세고 싶으면 `grep ^d`로 바꿔주면 되고, `.class`를 확장자로 가지는 파일을 찾고 싶으면 `grep ^-.*\.class$`로 바꿔주면 되겠다. (파일명 필터링에 관해서는 아래 부분에 또 다른 설명이 있다.) 정규식은 무궁무진한 표현이 가능하니 필요한 파일이나 디렉토리, 링크 등 원하는 것을 골라 잡자. ㅋㅋ

만일 위의 디렉토리에서 `.txt` 확장자를 가진 파일을 보고 싶다면 다음과 같이 입력하면 되겠다.

```bash
$ #설치된 grep 명령어의 종류에 따라 정규식에 따옴표를 사용해야 할 수도 있다.
$ ls -Rl | grep '^-.*\.txt$'
$ ls -Rl | grep "^-.*\.txt$"
$ ls -Rl | grep ^-.*\.txt$
-rw-r--r-- 1 root root 7155 11월 21 16:50 manual.txt
-rw-r--r-- 1 root root 53 11월 21 16:28 test.txt
```

하위 디렉토리의 파일도 검색하고 싶으니 `ls` 명령에 `-R` 옵션도 주었다. 그러면 현재 디렉토리의 하위에 있는 모든 `.txt` 확장자를 가진 파일이 검색된다. 자.. 이제 이 목록의 줄 수만 세면 `.txt` 확장자를 가진 파일의 개수를 알 수 있다.

자.. 필터링을 했으니 라인수를 세서 파일의 개수를 알아보자.

```bash
$ ls -l | grep ^- | wc -l
```

하위 디렉토리도 포함해서 ".txt" 확장자를 가진 파일을 세면.. 2개가 나온다.

```bash
$ ls -Rl | grep '^-.*\.txt$'
-rw-r--r-- 1 root root 7155 11월 21 16:50 manual.txt
-rw-r--r-- 1 root root 53 11월 21 16:28 test.txt
$ ls -Rl | grep '^-.*\.txt$' | wc -l
2
```

여기서 대문자로 시작하는 파일이나 숫자를 포함하는 파일명 등 조금 더 구체적으로 파일이나 디렉토리의 이름을 필터링해서 검색하려면 앞에 썼던 `grep` 명령어의 정규식을 잘 이용하면 되겠지만, 아래와 같이 `ls` 명령어의 형태로 출력이 되기 때문에 정규식을 만들기가 어렵다. 이 때 `awk` 명령으로 필요한 부분만 잘라내서 다시 `grep` 명령을 사용할 수 있다.

```bash
$ ls -Rl | grep ^-
-rw-r--r-- 1 root root 7155 11월 21 16:50 manual.txt
-rw-r--r-- 1 root root 237 11월 21 16:46 BlackSheepWall
-rw-r--r-- 1 root root 54 11월 21 16:45 OperationCWAL
-rw-r--r-- 1 root root 182 11월 21 16:44 ShowMeTheMoney
-rw-r--r-- 1 root root 2224 11월 21 16:28 test.jpg
-rw-r--r-- 1 root root 53 11월 21 16:28 test.txt

$ #"awk '{print $9}'"를 이용하여 파일명에 해당하는 9번째 열만을 뽑아내자.
$ ls -Rl | grep ^- | awk '{print $9}'
manual.txt
BlackSheepWall
OperationCWAL
ShowMeTheMoney
test.jpg
test.txt
```

그러면 파일명만이 남기 때문에 보다 수월하게 정규식을 이용하여 파일명을 필터링할 수 있다. 그럼 대문자로 시작하는 파일을 찾아보자. 개수는 3개..

```bash
$ ls -Rl | grep ^- | awk '{print $9}' | grep '^[A-Z]'
BlackSheepWall
OperationCWAL
ShowMeTheMoney
$ ls -Rl | grep ^- | awk '{print $9}' | grep '^[A-Z]' | wc -l
3
$ #파일명에 숫자가 포함된 파일 개수
$ ls -Rl | grep ^- | awk '{print $9}' | grep '^.*[0-9].*' | wc -l
```
