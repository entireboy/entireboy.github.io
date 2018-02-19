---
layout: post
title:  "[Shell] 배열 loop"
date:   2018-02-17 21:18:00 +0900
published: true
categories: [ shell ]
tags: [ shell, bash, loop, list, array ]
---

bash에서 배열 순환(loop)하는 방법
(매번 까먹는다 =_=)

```bash
$ cat test.sh
#!/bin/bash

for NAME in "ME" "YOU" "THEM" "ALL"; do
    echo "Name is ${NAME}"
done

PLANETS=( "EARTH" "MARS" "VINUS" )
for PLANET in ${PLANETS[@]}; do
    echo "This is ${PLANET}"
done
for (( i=0; i<${#PLANETS[@]}; i++ )); do
    echo "Planet #$i is ${PLANETS[i]}"
done

$ ./test.sh
Name is ME
Name is YOU
Name is THEM
Name is ALL
This is EARTH
This is MARS
This is VINUS
Planet #0 is EARTH
Planet #1 is MARS
Planet #2 is VINUS
```


# 참고

- [Array Loops in Bash](http://stackabuse.com/array-loops-in-bash/)
- [Advanced Bash-Scripting Guide: Chapter 11. Loops and Branches](http://tldp.org/LDP/abs/html/loops1.html)
