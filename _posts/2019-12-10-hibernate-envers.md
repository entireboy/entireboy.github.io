---
layout: post
title:  "[Hibernate] envers REV(revision number)를 long으로 바꾸기"
date:   2019-06-04 22:18:00 +0900
published: true
categories: [ hibernate ]
tags: [ hibernate, envers, audit, history, rev, revision, repository, fetch, lookup, find ]
---

Hibernate를 사용하면 entity의 변경(추가/변경/삭제)에 따른 [스냅샷(audit)을 남길 수 있다]({{ site.baseurl }}{% post_url 2019-06-04-hibernate-change-envers-REV-revision-number-to-long %}). 그런데, (당연하게도) 저장을 해뒀으니 이 이력을 조회하고 싶을 때가 간혹 있다.

그럴 땐 hibernate-envers 모듈 안에 함께 있는 `AuditQuery`를 사용해 보자. 주문(`Order`) entity를 조회하는데, `Order`의 ID가 아닌 사용자(`userId`)로 변경된 이력을 조회하고 싶을 때의 샘플이다.


```java

public class OrderRepositoryImpl extends QuerydslRepositorySupport implements OrderRepositoryCustom {

...

public List<OrderAuditDto> findOrderAuditsByShopNumber(Long userId) {
    AuditReader auditReader = AuditReaderFactory.get(getEntityManager());

    // 2번째 전달인자는 entity(여기서는 Order)만 조회할지 여부이고,
    // 3번째 전달인자는 delete 된 entity도 조회할지 여부이다.
    AuditQuery auditQuery = auditReader.createQuery()
        .forRevisionsOfEntity(Order.class, false, true);
    // 검색 조건
    auditQuery.add(AuditEntity.property("userId").eq(userId));
    // 정렬 조건
    auditQuery.addOrder(AuditEntity.revisionNumber().desc());
    // 페이징 - 다음 페이지로 넘어가면 검색 조건에
    auditQuery.setMaxResults(pageSize);
    // 검색 결과 - 결과는 Object []로 나오고, entity, revision entity, revision type 이 들어 있다.
    // 여기서는 Order, LongRevisionEntity, RevisionType
    List<Object[]> histories = auditQuery.getResultList();

    return histories.stream()
        .map(history -> OrderAuditDto.of(
            (Order) history[0],
            (LongRevisionEntity) history[1],
            (RevisionType) history[2]))
        .collect(toList());
}

}
```

`forRevisionsOfEntity` 메소드의 2번째 전달인자(`selectEntitiesOnly`)를 true로 주면 revision entity와 revision type 없이 entity만 리턴하고, false로 주면 entity와 revision entity, revision type을 함께 `Object []`로 준다. (캐스팅이 상당히 귀찮;;)

`forRevisionsOfEntity` 메소드는 오버라이딩된 여러 메소드가 있지만, incubating 상태인 녀석들이 많다. 조만간 정식으로 나오기를..

그리고 `forRevisionsOfEntityWithChanges` 메소드는 `Object []`이 4개로 늘어나고 마지막에는 entity의 어떤 property가 변경됐는지도 함께 준다.


# 참고

- [Hibernate Envers-Get all entities, revision numbers, revision dates and revision types of an Entity by its ID - Stack Overflow](https://stackoverflow.com/questions/44752324/hibernate-envers-get-all-entities-revision-numbers-revision-dates-and-revision)
- [Hibernate Envers – Query data from your audit log](https://thoughts-on-java.org/hibernate-envers-query-data-audit-log/)
- [Spring data jpa의 Audit 기능과 Spring data envers](https://gist.github.com/dlxotn216/94c34a2debf848396cf82a7f21a32abe)
- [[Hibernate] envers REV(revision number)를 long으로 바꾸기]({{ site.baseurl }}{% post_url 2019-06-04-hibernate-change-envers-REV-revision-number-to-long %})
