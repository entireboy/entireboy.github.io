---
layout: post
title:  "[MongoDB] TTL index의 expire time"
date:   2023-01-17 22:18:00 +0900
published: true
categories: [ mongodb ]
tags: [ mongodb, TTL index, index, expire, ttl ]
---

# TTL Index

MongoDB에는 document를 collection에 넣고 특정 시간이 지나면 삭제하는 [TTL index](https://www.mongodb.com/docs/v6.0/core/index-ttl/)가 있다.

[삭제 방식](https://www.mongodb.com/docs/v6.0/core/index-ttl/#delete-operations)이 독특하기 때문에 이 점을 염두에 두고 적용해야 한다.

## 백그라운드 삭제 스레드

`mongod`의 백그라운드 스레드가 삭제할 docuemnt를 찾아서 삭제한다. 백그라운드 스레드가 실행되면 `db.currentOp()`명령이나 database profiler를 통해서 삭제 명령이 실행되는 것을 확인할 수 있다.

단점은 백그라운드 스레드가 delete 커맨드를 마구 날려서 삭제하기 때문에 부하가 발생할 수 있다는 점이 있다.

Atlas MongoDB를 AWS에서 사용하는 경우 IOPS 제한에 걸릴 수 있다.


## 삭제 주기

백그라운드 스레드는 60초 마다 실행되기 때문에, expire 시간이 되었다고 해도 즉시 삭제되는 것이 아니라 백그라운드 스레드가 실행되기를 기다려야 한다. 삭제할 데이터가 많다면 삭제를 기다리는 시간도 있어 60초 보다 더 긴 시간이 걸릴 수 있다.


## Replica Sets

Document 수정은 Primary만 실행하기 때문에, Secondary node의 백그라운드 삭제 스레드는 실행되지 않고 idle 상태로 있는다. Secondary는 Primary에서 삭제된 정보를 복제 받아 반영한다.

> primary
>
> In a replica set, the primary is the member that receives all write operations.
>
> [primary - MongoDB Glossary](https://www.mongodb.com/docs/v6.0/reference/glossary/#std-term-primary)


# 테스트 1

TTL index를 추가하고 document를 하나 넣는다.

```javascript
use test;

// 5초 TTL index 설정
db.test.createIndex({
    createdAt: 1
}, {
    name: "IDX_EXPIRE_TEST",
    expireAfterSeconds: 5
});

db.test.insertOne({
    createdAt: new Date(),
    logEvent: 2,
    logMessage: "Success!"
});
```

현재 시각과 document가 생성된 시각을 함께 찍어본다. 시간이 흐르면 데이터가 삭제되는 것을 확인할 수 있고, 5초가 한참 지나도 데이터가 살아 있는 것을 알 수 있다.

```javascript
> new Date() + ' ' + db.test.findOne()?.createdAt
'Mon Jan 16 2023 15:02:10 GMT+0900 (Korean Standard Time) Mon Jan 16 2023 15:02:01 GMT+0900 (Korean Standard Time)'
> new Date() + ' ' + db.test.findOne()?.createdAt
'Mon Jan 16 2023 15:02:32 GMT+0900 (Korean Standard Time) undefined'
```


# 테스트 2

그렇다면 document를 먼저 넣고 TTL index의 expire time을 변경하면 어떻게 될까?

위에서 5초로 설정되어 있는 것을 180초로 변경해 본다.

```javascript
db.test.insertOne({
    createdAt: new Date(),
    logEvent: 2,
    logMessage: "Success!"
});

// 5초에서 180초로 변경
db.runCommand({
    collMod: "test",
    index: {
        name: "IDX_EXPIRE_TEST",
        expireAfterSeconds: 180
    }
});
```

같은 방법으로 언제까지 살아 있는지 확인하면 180초가 넘어야 삭제가 된다.

```javascript
> new Date() + ' ' + db.test.findOne()?.createdAt
'Mon Jan 16 2023 15:25:10 GMT+0900 (Korean Standard Time) Mon Jan 16 2023 15:22:01 GMT+0900 (Korean Standard Time)'
> new Date() + ' ' + db.test.findOne()?.createdAt
'Mon Jan 16 2023 15:25:11 GMT+0900 (Korean Standard Time) Mon Jan 16 2023 15:22:01 GMT+0900 (Korean Standard Time)'
> new Date() + ' ' + db.test.findOne()?.createdAt
'Mon Jan 16 2023 15:25:12 GMT+0900 (Korean Standard Time) undefined'
```


# 결론

- 문서에 expire time을 기록하고 그 시각이 넘으면 조회하지 않는 것이 아니라, 매 60초 마다 삭제 백그라운드 스레드가 동작한다.
- 백그라운드 스레드가 삭제할 대상을 선정하고 삭제를 delete 명령으로 실행한다.
- Expire time(`expireAfterSeconds` 설정값)을 변경해도 부하가 생기기 않고, 이미 존재하는 document에도 설정이 잘 먹는다.


# 참고

- [TTL index](https://www.mongodb.com/docs/v6.0/core/index-ttl/)
- [Expire Data from Collections by Setting TTL](https://www.mongodb.com/docs/v6.0/tutorial/expire-data/)
- [primary - MongoDB Glossary](https://www.mongodb.com/docs/v6.0/reference/glossary/#std-term-primary)
