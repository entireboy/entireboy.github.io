---
layout: post
title:  "[MySQL] rename PK column"
date:   2019-03-25 22:18:00 +0900
published: true
categories: [ mysql ]
tags: [ mysql, rename, column, PK, constraint, alter ]
---

PK가 달린 ID 컬럼명을 바꿀 일이 생겼다. 모든 테이블의 key 컬럼이 `ID` 라서 인식율이 떨여진다는 것이다.

```sql
sql> ALTER TABLE deal
CHANGE COLUMN id deal_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT;
```

위처럼 일반 컬럼 바꾸듯이 모든 constraints를 다 주고 실행했더니 아래 같은 오류가 발생한다.

```
Multiple primary key defined
```

PK constraint는 떼고 바꿔도 PK 컬럼명이 모두 잘 바뀐다.

```sql
sql> ALTER TABLE deal
CHANGE COLUMN id deal_id BIGINT NOT NULL AUTO_INCREMENT;
```

# 참고

- [How do I rename a primary key column in MySQL?](https://stackoverflow.com/questions/2703126/how-do-i-rename-a-primary-key-column-in-mysql)
