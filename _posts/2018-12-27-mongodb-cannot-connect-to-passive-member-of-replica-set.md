---
layout: post
title:  "[MongoDB] Replica set member 중에 접속 안 될 때는 passive member 체크"
date:   2018-12-27 22:18:00 +0900
published: true
categories: [ mongodb ]
tags: [ mongodb, replica set, cluster, passive, member, priority, close, connection, connect ]
---

# 현상

MongoDB 2대(`mongo1`, `mongo2`)와 arbiter 1대(`arbiter1`)로 구성된 replica set이 있다. 이상하게 현재 primary인 `mongo1`로만 read가 되어서 `primaryPreferred`가 read preference로 설정되어 있는가 했지만, `netstat` 등으로 보니 `mongo2`로는 커넥션 조차 맺어져 있지 않았다. 아무리 `primaryPreferred`라도 `mongo2`에서 read가 가능하도록 커넨션은 맺어져 있어야 하는데, 뭐가 문제일까??

```java
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Cluster created with settings {hosts=[mongo1.leocat.kr:27017, mongo2.leocat.kr:27017], mode=MULTIPLE, requiredClusterType=UNKNOWN, serverSelectionTimeout='30000 ms', maxWaitQueueSize=50}
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Adding discovered server mongo1.leocat.kr:27017 to client view of cluster
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Adding discovered server mongo2.leocat.kr:27017 to client view of cluster
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Opened connection [connectionId{localValue:36, serverValue:614791}] to mongo2.leocat.kr:27017
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Opened connection [connectionId{localValue:34, serverValue:7713772}] to mongo1.leocat.kr:27017
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Opened connection [connectionId{localValue:35, serverValue:7713773}] to mongo1.leocat.kr:27017
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Monitor thread successfully connected to server with description ServerDescription{address=mongo1.leocat.kr:27017, type=REPLICA_SET_PRIMARY, state=CONNECTED, ok=true, version=ServerVersion{versionList=[3, 4, 5]}, minWireVersion=0, maxWireVersion=5, maxDocumentSize=16777216, roundTripTimeNanos=1097648, setName='rs-test01', canonicalAddress=mongo1.leocat.kr:27017, hosts=[mongo1.leocat.kr:27017], passives=[], arbiters=[arbiter1.leocat.kr:27017], primary='mongo1.leocat.kr:27017', tagSet=TagSet{[]}, electionId=7fffffff0000000000000001, setVersion=5, lastWriteDate=Wed Dec 19 15:10:31 KST 2018, lastUpdateTimeNanos=12873051468492200}
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Discovered cluster type of REPLICA_SET
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Adding discovered server arbiter1.leocat.kr:27017 to client view of cluster
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Server mongo2.leocat.kr:27017 is no longer a member of the replica set.  Removing from client view of cluster.
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Opened connection [connectionId{localValue:37, serverValue:614792}] to mongo2.leocat.kr:27017
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Monitor thread successfully connected to server with description ServerDescription{address=mongo2.leocat.kr:27017, type=REPLICA_SET_OTHER, state=CONNECTED, ok=true, version=ServerVersion{versionList=[3, 4, 15]}, minWireVersion=0, maxWireVersion=5, maxDocumentSize=16777216, roundTripTimeNanos=547984, setName='rs-test01', canonicalAddress=mongo2.leocat.kr:27017, hosts=[mongo1.leocat.kr:27017], passives=[], arbiters=[arbiter1.leocat.kr:27017], primary='mongo1.leocat.kr:27017', tagSet=TagSet{[]}, electionId=null, setVersion=5, lastWriteDate=Wed Dec 19 15:10:31 KST 2018, lastUpdateTimeNanos=12873051472889680}
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Closed connection [connectionId{localValue:36, serverValue:614791}] to mongo2.leocat.kr:27017 because the pool has been closed.
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Setting max election id to 7fffffff0000000000000001 from replica set primary mongo1.leocat.kr:27017
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Setting max set version to 5 from replica set primary mongo1.leocat.kr:27017
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Discovered replica set primary mongo1.leocat.kr:27017
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Opened connection [connectionId{localValue:40, serverValue:4332545}] to arbiter1.leocat.kr:27017
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Opened connection [connectionId{localValue:41, serverValue:4332546}] to arbiter1.leocat.kr:27017
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Monitor thread successfully connected to server with description ServerDescription{address=arbiter1.leocat.kr:27017, type=REPLICA_SET_ARBITER, state=CONNECTED, ok=true, version=ServerVersion{versionList=[3, 4, 15]}, minWireVersion=0, maxWireVersion=5, maxDocumentSize=16777216, roundTripTimeNanos=1271940, setName='rs-test01', canonicalAddress=arbiter1.leocat.kr:27017, hosts=[mongo1.leocat.kr:27017], passives=[], arbiters=[arbiter1.leocat.kr:27017], primary='mongo1.leocat.kr:27017', tagSet=TagSet{[]}, electionId=null, setVersion=5, lastWriteDate=Wed Dec 19 15:10:31 KST 2018, lastUpdateTimeNanos=12873051480917747}
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Opened connection [connectionId{localValue:42, serverValue:4332547}] to arbiter1.leocat.kr:27017
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Opened connection [connectionId{localValue:43, serverValue:4332548}] to arbiter1.leocat.kr:27017
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Opened connection [connectionId{localValue:39, serverValue:7713776}] to mongo1.leocat.kr:27017
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Opened connection [connectionId{localValue:44, serverValue:4332549}] to arbiter1.leocat.kr:27017
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Opened connection [connectionId{localValue:46, serverValue:4332550}] to arbiter1.leocat.kr:27017
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Opened connection [connectionId{localValue:47, serverValue:4332551}] to arbiter1.leocat.kr:27017
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Opened connection [connectionId{localValue:48, serverValue:4332552}] to arbiter1.leocat.kr:27017
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Opened connection [connectionId{localValue:49, serverValue:4332553}] to arbiter1.leocat.kr:27017
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Opened connection [connectionId{localValue:45, serverValue:7713780}] to mongo1.leocat.kr:27017
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Opened connection [connectionId{localValue:50, serverValue:4332554}] to arbiter1.leocat.kr:27017
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Opened connection [connectionId{localValue:52, serverValue:4332555}] to arbiter1.leocat.kr:27017
```

로그를 보니 아래처럼 조금 이상한 메시지들이 섞여 있었다. `mongo2`가 replica set member가 아니라는 메시지와 함께 열어둔 커넥션을 닫아 버리는 것이다.

```java
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Opened connection [connectionId{localValue:36, serverValue:614791}] to mongo2.leocat.kr:27017
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Server mongo2.leocat.kr:27017 is no longer a member of the replica set.  Removing from client view of cluster.
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Closed connection [connectionId{localValue:36, serverValue:614791}] to mongo2.leocat.kr:27017 because the pool has been closed.
```

그리고 모든 서버(`mongo1`, `mongo2`, `arbiter1`)로 부터 받아온 서버 정보(`ServerDescription`)에 있는 `hosts`에도 `mongo2`는 없고 `mongo1`만 있다.


# 원인

```java
[2018-12-19 15:10:31] [INFO] [c.m.d.l.SLF4JLogger] Monitor thread successfully connected to server with description ServerDescription{address=mongo2.leocat.kr:27017, type=REPLICA_SET_OTHER, ...
```

위처럼 `mongo2`에서 받아온 정보에 `type`이 `REPLICA_SET_OTHER`인 것이 가장 의심스러워 찾아보니 [MongoDB client JavaDoc](http://api.mongodb.com/java/current/com/mongodb/connection/ServerType.html#REPLICA_SET_OTHER)에 이렇게 적혀 있었다.

> **REPLICA_SET_OTHER**
>
> A replica set member that is none of the other types (a passive, for example).
>
> (primary, secondary, arbiter 등) 다른 어떤 타입도 아닌 replica set member (예를 들어, passive).

`mongo2`는 replica set으로 묶이기는 했지만 `primary`도 `secondary`도 `arbiter`도 아닌 별도의 장비(`REPLICA_SET_OTHER`)였다. JavaDoc에 쓰여져 있듯이 `passive member` 같은.. [MongoDB doc](https://docs.mongodb.com/manual/reference/glossary/#term-passive-member)에 `passive member`는 primary가 될 수 없는 `priority`가 `0`인 member라고 되어 있다. 별도의 백업용이나 stand by 등으로 활용할 수 있을 것 같다. ([Priority 0 Replica Set Members](https://docs.mongodb.com/manual/core/replica-set-priority-0-member/))

> **passive member**
>
> A member of a replica set that cannot become primary because its members[n].priority is 0. See [Priority 0 Replica Set Members](https://docs.mongodb.com/manual/core/replica-set-priority-0-member/).
>
> Priority가 0이라 primary가 될 수 없는 replica set member. [Priority 0 Replica Set Members](https://docs.mongodb.com/manual/core/replica-set-priority-0-member/) 참고.

`passive`를 `secondary`로 설정을 바꾸면서 보니, `passive`도 `secondary`처럼 데이터는 모두 전달되어 있었다.


# 해결

[rs.conf()](https://docs.mongodb.com/manual/reference/method/rs.conf/)명령으로 member의 `priority`를 `1` 이상으로 변경한다.

읭?? 방법이 너무 짧..지만 이게 끝;; 원인 찾는데만 시간을 너무 써버림 T_T


# 참고

- [com.mongodb.connection.ServerType JavaDoc](http://api.mongodb.com/java/current/com/mongodb/connection/ServerType.html#REPLICA_SET_OTHER)
- [MongoDB Glossary](https://docs.mongodb.com/manual/reference/glossary/#term-passive-member)
- [Priority 0 Replica Set Members](https://docs.mongodb.com/manual/core/replica-set-priority-0-member/)
- [rs.conf() - MongoDB replication method](https://docs.mongodb.com/manual/reference/method/rs.conf/)
- [Replica Set Configuration](https://docs.mongodb.com/manual/reference/replica-configuration/)
