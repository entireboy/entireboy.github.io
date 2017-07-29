---
layout: post
title:  "[SQL] join의 on절과 where절 차이"
date:   2017-07-28 22:18:00 +0900
published: true
categories: [ sql ]
tags: [ sql, db, database, query, subtract, minus, except ]
---

> 이전 블로그에서 옮겨온 포스트

SQL의 JOIN에서 `ON`과 `WHERE`의 차이점은 JOIN하는 범위가 다르다.
아래 두 SQL문을 보자. 두 SQL문 모두 LEFT JOIN을 수행하는 OUTER JOIN이다.

```sql
1)
SELECT *
FROM test1 a LEFT JOIN test2 b
ON (a.aa = b.aa)
WHERE b.cc = 7;

2)
SELECT *
FROM test1 a LEFT JOIN test2 b
ON (a.aa = b.aa AND b.cc = 7);
```

1)의 경우는 a와 b 테이블의 OUTER JOIN을 수행한 후에 b.cc = 7인 데이터들을 추출하지만
2)의 경우는 (a 테이블)과 (b 테이블 중 b.cc = 7인 경우)를 OUTER JOIN 한 결과가 나온다.

따라서 1)의 결과는 b.dd = 7인 데이터만 존재하지만 2)의 결과는 b.cc = 7이 아닌 데이터도 존재한다. 아래와 같은 `test1`, `test2` 테이블이 있을 때,

```sql
test1      test2
aa|bb      aa|cc
-----      -----
1 | 4      1 | 7
2 | 5      2 | 8
3 | 6
```

그 SQL의 결과는 다음과 같다.

```sql
1)
1 | 4 | 1 | 7

2)
1 | 4 | 1    | 7
2 | 5 | null | null
3 | 6 | null | null
```

**한마디로 ON과 WHERE의 경우는 JOIN을 할 대상(범위)이 달라진다는 것이다.**

{% include google-ad-content %}

이 점을 이용해서 LEFT OUTER JOIN으로 `차집합`을 구현할 수 있다. 오라클이나 MSSQL과 같은 경우는 `EXCEPT` 혹은 `MINUS` 등을 사용하면 되겠지만, MySQL은 버전에 따라 지원하는 경우도 있고 아닌 경우도 있다.

`test1` 테이블의 데이터 중 `test2` 테이블에 있는 데이터를 제외하고 가져오고 싶다. 위의 테이블에서 JOIN하는 column을 기준으로 1, 2는 `test2` 테이블에도 있으니 제외하고, `3 | 6`만을 가져오고 싶은 경우이다.

```sql
SELECT *
FROM test1 a LEFT JOIN test2 b
ON (a.aa = b.aa)
WHERE b.aa IS NULL;
```

```sql
3 | 6 | null | null
```

`test2.aa`에 있는 1, 2의 데이터를 제외한 데이터를 `test1.aa`에서 가져왔다. LEFT OUTER JOIN이기 때문에 WHERE절 이전까지 실행했을 때 아래와 같은 결과가 나온다.

```sql
SELECT *
FROM test1 a LEFT JOIN test2 b
ON (a.aa = b.aa);

1 | 4 | 1    | 7
2 | 5 | 2    | 8
3 | 6 | null | null
```

여기에서 `test2` 테이블에 존재하지 않아서 `test2` 테이블의 column이 `null`인 부분만을 가지고 오게 WHERE절을 달아주면!! 차집합이 된다는 것이다.

참 쉽죠??

![밥아저씨 - 참 쉽죠??](/assets/img/2017-07-28-sql-join-on-vs-where.gif)
