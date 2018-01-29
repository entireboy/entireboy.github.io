---
layout: post
title:  "[Shell] string 일부분 제거하기 (%, %%, #, ## 연산자)"
date:   2018-01-18 21:18:00 +0900
published: true
categories: [ shell ]
tags: [ shell, bash, remove, delete, string, substring, operator ]
---

bash script에서 문자열을 맨앞이나 맨뒤에서 부터 잘라내 버리고 싶은 경우가 있다. 요놈들을 참조하면 원하는 만큼 매치된 문자열을 제거할 수 있다.

```bash
${string#substring} - 맨앞에서부터 가장 짧게 매치된 문자열을 지운다.
${string##substring} - 맨앞에서부터 가장 길게 매치된 문자열을 지운다.
${string%substring} - 맨뒤에서부터 가장 짧게 매치된 문자열을 지운다.
${string%%substring} - 맨뒤에서부터 가장 길게 매치된 문자열을 지운다.
```

간단한 예제로 앞에서 부터 짤라보자.

```bash
STR=ABC123abc123ABC
#   |--|            가장 짧은거 (#)
#   |--------|      가장 긴거 (##)

echo ${STR#A*1}     # 23abc123ABC
echo ${STR##A*1}    # 23ABC
```

이번엔 뒤에서 부터 짤라보자.

```bash
STR=ABC123abc123ABC
#              |--| 가장 짧은거 (%)
#        |--------| 가장 긴거 (%%)

echo ${STR%3*C}     # ABC123abc12
echo ${STR%%3*C}    # ABC12
```

맨앞이나 맨뒤에서부터 매칭되지 않으면 잘리지 않는다.

```bash
echo ${STR#a*c}     # ABC123abc123ABC
echo ${STR%%a*c}    # ABC123abc123ABC
```


# 참고

- [Advanced Bash-Scripting Guide: Chapter 10. Manipulating Variables](http://tldp.org/LDP/abs/html/string-manipulation.html)
