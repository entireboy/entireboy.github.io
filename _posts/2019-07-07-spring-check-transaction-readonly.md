---
layout: post
title:  "[spring] transaction이 readonly인지 확인"
date:   2019-07-07 22:18:00 +0900
published: true
categories: [ spring ]
tags: [ spring, transaction, readonly, mark, flag ]
---

현재 사용 중인 transaction이 정말 readonly로 설정되었는지 확인하고 싶을 때, `TransactionSynchronizationManager#isCurrentTransactionReadOnly`를 사용해서 확인이 가능하다.

```java
import org.springframework.transaction.support.TransactionSynchronizationManager;


@Transactional(readOnly = true)
public void testReadOnly() {
    log.info("Transaction readonly flag: {}",
        TransactionSynchronizationManager.isCurrentTransactionReadOnly());

    // do my task
}
```

[TransactionSynchronizationManager](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/transaction/support/TransactionSynchronizationManager.html)를 통해서 isolation level(`getCurrentTransactionIsolationLevel`) 이나 transaction 이름(`getCurrentTransactionName`)도 얻어올 수 있다.


# 참고

- [TransactionSynchronizationManager - spring javadoc](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/transaction/support/TransactionSynchronizationManager.html)
