---
layout: post
title:  "[RegEx] Find x times patterns in text"
date:   2017-03-13 22:53:00 +0900
published: true
categories: [ regex ]
tags: [ regex, pattern ]
---

동일한 문자나 단어가 X번 이상 들어간 것들을 찾아야 할 때가 있다.

- `i`가 3번 이상 포함된 문장 찾기
```
/(i)(.*\1){2}/
```
`\1`을 사용해서 이전 captured group에 잡힌 문자를 다시 사용했다. 3번 이상인데 {2}인 이유는 이미 처음 그룹에 하나가 잡혔기 때문에 1을 줄인 것이다. `.*`을 사용한 이유는 `iii`처럼 `i`사이에 아무 글자가 없는 경우도 찾을 수 있게 하기 위함이다.

-  `item`이 2번 이상 포함된 문장 찾기
```
/(item)(.*\1){1}/
```
문자 하나 뿐만 아니라 단어도 가능하다.

- 어떤 문자든 동일한 문자가 4번 이상 포함된 문장 찾기
```
/(.)(.*\1){3}/
```
어떤 문자든 상관 없이 여러번 나온 횟수만 체크하고 싶다면 `.`을 쓰면 된다.

# 참고

- [Regex to find any character used more than 3 times in a string but not consecutively - Stack Overflow](http://stackoverflow.com/questions/1843506/regex-to-find-any-character-used-more-than-3-times-in-a-string-but-not-consecuti)
- [RegExr](http://regexr.com/) RegEx 패턴 테스트
