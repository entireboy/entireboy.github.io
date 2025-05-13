---
layout: post
title:  "[MySQL] 테이블 캐릭터셋(charset) 변경하기"
date:   2025-05-12 22:18:00 +0900
published: true
categories: [ mysql ]
tags: [ mysql, charset, collation ]
---

MySQL 테이블을 생성할 때 charset 설정을 깜빡하는 경우가 있다.

보통.. 이모지를 넣을 수 있게 만들었어야 했는데 테이블 만들 때 깜빡하는 경우..

# 현재 charset 확인하고 변경하기

```sql
SHOW TABLE STATUS WHERE name LIKE 'member%';
```

Collation 컬럼을 확인하면 charset을 알 수 있다.

```sql
ALTER TABLE member CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```


# 참고

- [MySQL: Get character-set of database or table or column?](https://stackoverflow.com/questions/1049728/mysql-get-character-set-of-database-or-table-or-column)
- [How to change the default collation of a table?](https://stackoverflow.com/questions/742205/how-to-change-the-default-collation-of-a-table)
