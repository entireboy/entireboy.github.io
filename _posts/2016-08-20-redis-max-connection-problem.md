---
layout: post
title:  "[Redis] Max Connection Problem"
date:   2016-08-20 21:01:00 +0900
published: true
categories: [ redis ]
tags: [ redis, connection, max, client, list, kill, sentinel ]
---

개발 환경처럼 여럿이 사용하고 있는 Redis에 접속을 하다보면, 가끔 이런 메시지와 함께 접속이 불가능한 경우가 있다.

```bash
Caused by: redis.clients.jedis.exceptions.JedisDataException: ERR max number of clients reached
   at redis.clients.jedis.Protocol.processError(Protocol.java:104) ~[jedis-2.4.2.jar:na]
   at redis.clients.jedis.Protocol.process(Protocol.java:122) ~[jedis-2.4.2.jar:na]
   at redis.clients.jedis.Protocol.read(Protocol.java:191) ~[jedis-2.4.2.jar:na]
   at redis.clients.jedis.Connection.getRawObjectMultiBulkReply(Connection.java:221) ~[jedis-2.4.2.jar:na]
   at redis.clients.jedis.Connection.getObjectMultiBulkReply(Connection.java:227) ~[jedis-2.4.2.jar:na]
   at redis.clients.jedis.Jedis.sentinelGetMasterAddrByName(Jedis.java:2950) ~[jedis-2.4.2.jar:na]
   at redis.clients.jedis.JedisSentinelPool.initSentinels(JedisSentinelPool.java:131) ~[jedis-2.4.2.jar:na]
   at redis.clients.jedis.JedisSentinelPool.(JedisSentinelPool.java:73) ~[jedis-2.4.2.jar:na]
   at redis.clients.jedis.JedisSentinelPool.(JedisSentinelPool.java:49) ~[jedis-2.4.2.jar:na]
```

이런.. 망.. 원인은 서버의 [max client 개수 설정](http://redis.io/topics/clients#maximum-number-of-clients)을 넘었기 때문인데, **실제 사용되지 않는 client 접속을 끊어주면 접속 가능**하다. (sentinel을 사용하는 경우는 맨 아래에 따로 설명)

[client list](http://redis.io/commands/client-list) 명령([http://redis.io/commands/client-list](http://redis.io/commands/client-list))으로 접속된 client 목록을 확인할 수 있다. 간혹, 네트웍 문제인지 모르겠지만 연결이 끊어졌지만 접속되어 있는 것처럼 redis server가 목록을 가지고 있는 녀석들도 보인다. 확실히 끊어진 녀석들을 골라서 죽여주면 접속이 가능하다. [client kill](http://redis.io/commands/client-kill) 명령([http://redis.io/commands/client-kill](http://redis.io/commands/client-kill))을 쓰면 연결을 끊을 수 있다.

```bash
127.0.0.1:6379> client list
id=3 addr=127.0.0.1:57288 fd=6 name= age=10 idle=10 flags=N db=0 sub=0 psub=0 multi=-1 qbuf=0 qbuf-free=0 obl=0 oll=0 omem=0 events=r cmd=client
id=4 addr=127.0.0.1:57289 fd=7 name= age=3 idle=0 flags=N db=0 sub=0 psub=0 multi=-1 qbuf=0 qbuf-free=32768 obl=0 oll=0 omem=0 events=r cmd=client
id=5 addr=127.0.0.1:57290 fd=6 name= age=2 idle=2 flags=N db=0 sub=0 psub=0 multi=-1 qbuf=0 qbuf-free=0 obl=0 oll=0 omem=0 events=r cmd=info
127.0.0.1:6379> client kill addr 127.0.0.1:57288
(integer) 1
127.0.0.1:6379> client list
id=4 addr=127.0.0.1:57289 fd=7 name= age=25 idle=0 flags=N db=0 sub=0 psub=0 multi=-1 qbuf=0 qbuf-free=32768 obl=0 oll=0 omem=0 events=r cmd=client
id=5 addr=127.0.0.1:57290 fd=6 name= age=2 idle=2 flags=N db=0 sub=0 psub=0 multi=-1 qbuf=0 qbuf-free=0 obl=0 oll=0 omem=0 events=r cmd=info
127.0.0.1:6379> client kill id 5
(integer) 1
127.0.0.1:6379> client list
id=4 addr=127.0.0.1:57289 fd=7 name= age=54 idle=0 flags=N db=0 sub=0 psub=0 multi=-1 qbuf=0 qbuf-free=32768 obl=0 oll=0 omem=0 events=r cmd=client
```

최근 삽질한건 **Redis sentinel을 사용하는 경우**!! sentinel을 사용하면 sentinel이 client 접속 컨트롤을 하기 때문에 sentinel에 죽은 client들이 붙어 있어서 접속이 안 되는 경우가 있다. SENTINEL client 명령으로 리스트 개수를 확인해 보고, sentinel에서도 제거해 주자.

Redis server의 설정을 변경할 수 있다면, maxclients를 늘려주자. 2.4 이하 버전을 쓰고 있다면 아쉽지만 하드코딩 되어 있어서 변경이 불가능한 것 같고, 2.6 이상 버전은 redis.conf 파일의 maxclients를 설정하면 된다. 설정하지 않으면 default로 10000개이다. ([http://redis.io/topics/clients#maximum-number-of-clients](http://redis.io/topics/clients#maximum-number-of-clients))
