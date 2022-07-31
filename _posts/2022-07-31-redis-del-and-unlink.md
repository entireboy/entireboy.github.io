---
layout: post
title:  "[Redis] 삭제: DEL과 UNLINK"
date:   2022-07-31 22:18:00 +0900
published: true
categories: [ redis ]
tags: [ redis, remove, delete, del, unlink, command, keyspace ]
---

Redis에서 key를 삭제하려면 DEL 명령어를 사용하면 된다. 그리고 UNLINK 명령어도 있다.

> **TL;DR**: DEL 보다는 UNLINK를 먼저 고려해 보자.


# UNLINK

UNLINK는 4.0.0 버전부터 추가되었고, 몇 개의 키를 삭제하든 **O(1)** 의 시간 밖에 필요하지 않다. **DEL 보다 빠르다.**

커맨드를 실행할 때는 삭제할 키를 keyspace에서만 삭제하고 실제로 데이터를 지우는 것은 비동기 형태로 다른 스레드에서 진행하게 되어, 데이터 삭제가 UNLINK 명령을 블로킹하지 않는다. 데이터를 삭제하는 스레드는 O(N)의 시간복잡도를 갖는다.

존재하는 키만 삭제하기 때문에, 키가 없어도 에러가 발생하지 않으며 삭제된 키의 개수만을 리턴값으로 받는다.

[ACL 카테고리](https://redis.io/commands/acl-cat/)에서 `@fast` 카테고리에 속한다.


# DEL

DEL은 태초(?? 1.0.0 버전)부터 있던 명령어이고, 삭제할 키를 찾고 지우기 위해 **O(N)** 의 시간이 필요하다. 문자열로 된 key를 하나만 삭제하면 O(1)이 걸린다. list, set과 같은 다른 타입을 사용하면 element 개수(M)에 따른 O(M)의 시간 복잡도를 가진다. **UNLINK 보다 느리다.**

블로킹 형태로 동작하며, 명령을 실행하면 바로 키를 찾아서 삭제하고 삭제된 키의 개수를 반환한다.

DEL 역시 존재하는 키만 삭제하기 때문에, 키가 없어도 에러가 발생하지 않으며 삭제된 키의 개수를 리턴 받는다.

[ACL 카테고리](https://redis.io/commands/acl-cat/)에서 `@slow` 카테고리에 속한다.


# 결론

UNLINK는 데이터 삭제를 비동기로 처리하여, 커맨드 응답 속도를 개선하였다.

string 타입의 key 1개를 삭제하는 경우 DEL, UNLINK는 동일한 시간 O(1)이 걸린다.

삭제하는 key 개수가 늘어나면 DEL은 시간이 점점 오래 걸린다.

list, set, hash와 같이 string 이외의 타입을 가지면 DEL은 그 element 개수 만큼 시간이 걸린다.


# 참고

- [UNLINK key [key ...]](https://redis.io/commands/unlink/)
- [DEL key [key ...]](https://redis.io/commands/del/)
- [ACL CAT [categoryname]](https://redis.io/commands/acl-cat/)
