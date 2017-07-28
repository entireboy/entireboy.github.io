---
layout: post
title:  "[Oracle] RANK() OVER vs ROW_NUMBER() OVER"
date:   2017-07-20 22:18:00 +0900
published: true
categories: [ oracle ]
tags: [ oracle, rank, query, row number, db ]
---

> 이전 블로그에서 옮겨온 포스트 (일부 동작 안 하는 샘플 수정)

레코드를 특정 column의 값을 기준으로 정렬하여 순서를 매길 수 있다. `RANK() OVER` 또는 `ROW_NUMBER() OVER`를 사용하면 된다. 간단히 아래와 같은 테이블을 생각하자.

```sql
SQL> SELECT * FROM TMP_TABLE;

USERID    |     SCORE
----------|----------
aaa       |        10
aaa       |        30
aaa       |        50
aaa       |        90
bbb       |        80
bbb       |        50
bbb       |        20
bbb       |        40
aaa       |        50

9 rows selected.
```

score column을 기준으로 순서를 매기고 싶다. 그러면 간단하게 다음과 같이 하면 되는데, `RANK() OVER`와 `ROW_NUMBER() OVER` 둘의 순서값에 조금 차이가 있다.

```sql
SQL> SELECT
    USERID,
    SCORE,
    RANK() OVER (ORDER BY SCORE DESC) RANK
FROM TMP_TABLE;

USERID    |     SCORE|      RANK
----------|----------|----------
aaa       |        90|         1
bbb       |        80|         2
aaa       |        50|         3
aaa       |        50|         3
bbb       |        50|         3
bbb       |        40|         6
aaa       |        30|         7
bbb       |        20|         8
aaa       |        10|         9

9 rows selected.
```

```sql
SQL> SELECT USERID,
    SCORE,
    ROW_NUMBER() OVER (ORDER BY SCORE DESC) RANK
FROM TMP_TABLE;

USERID    |     SCORE|      RANK
----------|----------|----------
aaa       |        90|         1
bbb       |        80|         2
aaa       |        50|         3
aaa       |        50|         4
bbb       |        50|         5
bbb       |        40|         6
aaa       |        30|         7
bbb       |        20|         8
aaa       |        10|         9

9 rows selected.
```

위의 `RANK()`와 `ROW_NUMBER()`의 차이는 결과를 자세히 보면 알 수 있다. `RANK()`의 결과는 3, 4, 5번째 레코드의 점수가 **50점으로 동일하기 때문에 때문에 같은 순서인 3번**을 매겼다. 하지만 `ROW_NUMBER()`의 결과는 **점수가 같더라도 레코드가 달라지면 다른 순서**를 매긴다.
