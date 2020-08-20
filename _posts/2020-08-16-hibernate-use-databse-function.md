---
layout: post
title:  "[Hibernate] DB 함수 사용하기"
date:   2020-08-16 21:18:00 +0900
published: true
categories: [ hibernate ]
tags: [ hibernate, querydsl, databse, db, function, mysql, group_concat ]
---

# 문제

Hibernate를 사용할 때 `group_concat`과 같은 database function을 사용하면 아래처럼 오류가 난다.

```bash
[select adBillingTransaction.shopOwnerNumber, adBillingTransaction.billingDate, 쏼라쏼라, group_concat(adBillingTransaction.id)


from kr.leocat.test.model.AdBillingTransaction adBillingTransaction
  inner join fetch adBillingTransaction.adBillings as adBilling


group by adBillingTransaction.shopOwnerNumber, adBillingTransaction.billingDate]; nested exception is java.lang.IllegalArgumentException: org.hibernate.QueryException: No data type for node: org.hibernate.hql.internal.ast.tree.MethodNode
 \-[METHOD_CALL] MethodNode: '('
    +-[METHOD_NAME] IdentNode: 'group_concat' {originalText=group_concat}
    \-[EXPR_LIST] SqlNode: 'exprList'
       \-[DOT] DotNode: 'adbillingt0_.id' {propertyName=id,dereferenceType=PRIMITIVE,getPropertyPath=id,path=adBillingTransaction.id,tableAlias=adbillingt0_,className=kr.leocat.test.model.AdBillingTransaction,classAlias=adBillingTransaction}
          +-[ALIAS_REF] IdentNode: 'adbillingt0_.id' {alias=adBillingTransaction, className=kr.leocat.test.model.AdBillingTransaction, tableAlias=adbillingt0_}
          \-[IDENT] IdentNode: 'id' {originalText=id}
```

코드는 아래처럼 querydsl로 `group_concat`을 사용했다.

```java
return from(adBillingTransaction)
    .join(adBillingTransaction.adBillings, adBilling)
    .where(adBillingTransaction.adCostType.eq(REGULAR)
        .and(adBillingTransaction.billingDate.in(billingDates)))
    .orderBy(adBillingTransaction.billingDate.desc(), adBillingTransaction.shopOwnerNumber.asc())
    .groupBy(adBillingTransaction.shopOwnerNumber, adBillingTransaction.billingDate)
    .select(Projections.constructor(RegularPayHistoryDto.class,
        adBillingTransaction.shopOwnerNumber,
        adBillingTransaction.billingDate,
        쏼라쏼라,
        Expressions.stringTemplate("group_concat({0})", adBilling.id)))
    .fetch();
```


# 해결

특정 버전 이후 부터는 hibernate에서 자동으로 해주는걸로 알고 있는데, 안된다. T_T 결국 `group_concate` 함수를 설정을 통해 등록해 주니 잘 동작한다.

```java
import org.hibernate.dialect.MySQL57Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StringType;

public class AdBillingMysql57Dialect extends MySQL57Dialect {

    public AdBillingMysql57Dialect() {
        super();

        // native function 추가 - hibernate 버그인지 등록을 안 해주면 실행을 못 함
        this.registerFunction("group_concat", new StandardSQLFunction("group_concat", new StringType()));
    }

}
```


# 참고

- [How to call mysql function using querydsl?](https://stackoverflow.com/questions/22984343/how-to-call-mysql-function-using-querydsl)
- [How to call custom database functions with JPA and Hibernate](https://thoughts-on-java.org/database-functions/)
- [GROUP_CONCAT does NOT work in QueryDSL](https://github.com/querydsl/querydsl/issues/2377)
