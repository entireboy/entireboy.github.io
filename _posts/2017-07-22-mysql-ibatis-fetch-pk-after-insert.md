---
layout: post
title:  "[MySQL,MyBatis] LAST_INSERT_ID() - insert 후 PK값 얻어오기"
date:   2017-07-22 21:18:00 +0900
published: true
categories: [ mysql, Mybatis ]
tags: [ mysql, mybatis, LAST_INSERT_ID, insert, pk ]
---

> 이전 블로그에서 옮겨온 포스트 (일부 동작 안 하는 샘플 수정)

테이블이 2개가 있다. A 테이블의 PK를 B 테이블이 FK로 참조하고 있을 때, A 테이블에 1개의 row를 추가하면서 B 테이블에도 1개의 row를 함께 추가하는 경우가 있다. 어떻게 해야 할까?? Oracle이라면 시퀀스를 사용해서 시퀀스를 먼저 뽑아내 두 테이블에 동일한 값을 설정하여 추가하면 되겠지만.. MySQL의 AUTO_INCREMENT로 설정된 column은 그 값을 먼저 가져올 수도 없다.

이럴 때는 MySQL의 LAST_INSERT_ID()라는 함수를 사용하면 된다. 이 함수는 가장 최근에 성공적으로 수행된 INSERT 구문의 첫번째 AUTO_INCREMENT column의 값을 반환한다. [MySQL 메뉴얼](https://dev.mysql.com/doc/refman/5.7/en/information-functions.html#function_last-insert-id)에는 이렇게 쓰여 있다.

> With no argument, LAST_INSERT_ID() returns a BIGINT UNSIGNED (64-bit) value representing the first automatically generated value successfully inserted for an AUTO_INCREMENT column as a result of the most recently executed INSERT statement.

간단한 예제를 보자.

```sql
mysql> USE test;
Database changed
mysql> CREATE TABLE t (
    ->   id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    ->   name VARCHAR(10) NOT NULL
    -> );
Query OK, 0 rows affected (0.09 sec)

mysql> INSERT INTO t VALUES (NULL, 'Bob');
Query OK, 1 row affected (0.01 sec)

mysql> SELECT * FROM t;
+----+------+
| id | name |
+----+------+
|  1 | Bob  |
+----+------+
1 row in set (0.01 sec)

mysql> SELECT LAST_INSERT_ID();
+------------------+
| LAST_INSERT_ID() |
+------------------+
|                1 |
+------------------+
1 row in set (0.00 sec)

mysql> INSERT INTO t VALUES
    -> (NULL, 'Mary'), (NULL, 'Jane'), (NULL, 'Lisa');
Query OK, 3 rows affected (0.00 sec)
Records: 3  Duplicates: 0  Warnings: 0

mysql> SELECT * FROM t;
+----+------+
| id | name |
+----+------+
|  1 | Bob  |
|  2 | Mary |
|  3 | Jane |
|  4 | Lisa |
+----+------+
4 rows in set (0.01 sec)

mysql> SELECT LAST_INSERT_ID();
+------------------+
| LAST_INSERT_ID() |
+------------------+
|                2 |
+------------------+
1 row in set (0.00 sec)
```

처음 INSERT 구문으로 `Bob`을 추가하고 `LAST_INSERT_ID()`의 반환값은 `1`이 나왔다. AUTO_INCREMENT column의 id값이다. 그리고 2번째 INSERT 구문으로 `Mary`와 `Jane`, `Lisa`를 추가하였다. 이 때는 3개의 row를 추가했고, 이 추가된 record 중 가장 처음에 추가된 record의 id인 2가 반환된다.

{% include google-ad-content %}

그리고 이 `LAST_INSERT_ID()`의 값은 각 connection 마다 따로 관리된다. 때문에 A와 B가 다른 connection을 가지고 동시에 INSERT 후 `LAST_INSERT_ID()`값을 SELECT하더라도 자신이 INSERT한 ID값을 반환받게 된다. 예를 들면, A가 INSERT를 하고 `LAST_INSERT_ID()`를 SELECT하려고 하는 순간 B가 먼저 INSERT를 하였다. 이 경우 A가 SELECT하여 얻어지는 ID값은 B와는 무관하게 A가 INSERT한 ID값이 된다. 물론, 전제 조건은 A와 B가 다른 connection을 사용한다는 것이지만, 동시에 INSERT하고 접근하는 문제가 있어도 유용하게 사용할 수 있다.

또한 MyBatis에서 INSERT 이후 ID를 바로 반환하여 사용할 수 있다. 다음과 같이 `selectKey`를 이용하여 `sqlMap`을 설정한다.
```xml
<insert id="user.insertUserAndGetId" parameterClass="user">
    <![CDATA[
        INSERT INTO t
        (
            name
        )
        VALUES
        (
            #name#
        )
    ]]>
    <selectKey keyProperty="id" resultClass="Integer">
        SELECT LAST_INSERT_ID()
    </selectKey>
</insert>
```

```java
UserVO user = new UserVO();
user.setName("Bob");

System.out.println("Index of user " + user.getName() + " is " + user.getId() + " (before insert)");
int id = ((Integer) sqlMap.insert("user.insertUserAndGetId")).intValue();
System.out.println("Index of user " + user.getName() + " is " + user.getId() + " (after insert)");
```

해당 INSERT 구문을 수행하고 selectKey에 정의한 구문을 수행하여 그 값을 반환한다. selectKey의 속성을 살펴보면, `keyProperty`와 `resultClass`가 있다.

keyProperty 속성을 설정해 주면 selectKey로 SELECT된 값을 전달인자의 변수값으로 설정해 준다. 소스코드의 user는 이름만 `Bob`으로 설정해 주었다. 하지만 INSERT를 수행한 다음 `user.getId()`의 값이 추가된 ID로 설정된 것을 볼 수 있다. 이 속성은 설정하는 경우만 해당 변수의 값(이 예제에서는 user의 id 변수)이 설정되므로 설정하지 않아도 문제는 없다.

```bash
Index of user Bob is 0 (before insert)
Index of user Bob is 7 (after insert)
```

resultClass는 `selectKey`로 반환되는 값의 타입을 나타낸다. 이 타입과 동일한 타입으로 소스코드에서 casting해서 받으면 된다. `LAST_INSERT_ID()`로 반환되는 값은 숫자이니 int로 받으면 될 것이다. 물론 `LAST_INSERT_ID()`는 MySQL 함수이기 때문에 MySQL인 경우만 써야 한다. MySQL이 아닌 경우라면 INSERT된 값의 PK를 구할 수 있는 SELECT 구문을 적어주면 된다.

# 참고

- MySQL 메뉴얼 : 12.14 Information Functions [LAST_INSERT_ID(), LAST_INSERT_ID(expr)](https://dev.mysql.com/doc/refman/5.7/en/information-functions.html#function_last-insert-id)
