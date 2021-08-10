---
layout: post
title:  "[Redis] Master 이외의 Replica(slave)로 부터 읽기"
date:   2021-08-10 22:18:00 +0900
published: true
categories: [ redis ]
tags: [ redis, read, replica, slave, master, java, driver, lettuce, redisson, jedis, ha ]
---

master-replica(master-slave) 구조로 HA 구성을 해둔 Redis에서, 일반적으로 read/write 작업을 하게 되면 master에서만 동작을 하게 된다. 사실 replica가 여러개 있어도 성능에 도움이 되지 못 하는 형태이다.

Replication lag을 감안해도 되는 경우라면 read 작업은 replica(slave)에서 해도 되는 경우가 많다. 일부 master 서버에만 몰리는 트래픽을 분산시킬 수도 있다. single thread이기 때문에 발생할 수 있는, read 작업이 몰릴 때 master에서 다른 요청이 끝나기를 기다리는 경우를 피할 수도 있다.

읽기 작업을 reaplica에서 하려면 각 드라이버에서 간단한 설정으로 가능하다.


# Lettuce ReadFrom 설정

Lettuce 드라이버는 [ReadFrom](https://github.com/lettuce-io/lettuce-core/wiki/ReadFrom-Settings) 설정으로 어디에서 읽어올지 선택이 가능하다.

```java
RedisURI masterUri = RedisURI.Builder.redis("master-host", 6379).build();
RedisClient client = RedisClient.create();

StatefulRedisMasterReplicaConnection<String, String> connection = MasterReplica.connect(
            client,
            StringCodec.UTF8,
            masterUri);

connection.setReadFrom(ReadFrom.REPLICA);

connection.sync().get("key"); // Replica read

connection.close();
client.shutdown();
```

`ReadFrom`에 설정 가능한 값은:

- UPSTREAM: 현재 master 노드에서만 읽음. (default)
- UPSTREAM_PREFERRED: master 노드를 우선으로 읽음. 불가능할 경우, replica 노드에서 읽음.
- REPLICA: replica 노드에서만 읽음.
- REPLICA_PREFERRED: replica 노드를 우선으로 읽음. 불가능할 경우, master 노드에서 읽음.
- NEAREST: 가장 latency가 낮은 (가까운) 노드에서 읽음.
- ANY: 클러스터의 아무 노드에서나 읽음.
- ANY_REPLICA: 클러스터의 아무 replica 노드에서나 읽음.


# Redisson ReadMode 설정

Redisson 드라이버는 [readMode](https://redisson.org/glossary/redis-master-slave-replication.html) 설정으로 읽어올 노드 선택이 가능하다.

```java
Config config = new Config();
config.useMasterSlaveServers()
    // use "rediss://" for SSL connection
    .setMasterAddress("redis://127.0.0.1:6379")
    .addSlaveAddress("redis://127.0.0.1:6389", "redis://127.0.0.1:6332", "redis://127.0.0.1:6419")
    .addSlaveAddress("redis://127.0.0.1:6399")
    .setReadMode(ReadMode.SLAVE);
```

yaml 파일로 설정하는 경우 아래처럼 `readMode`를 설정하면 된다.

```yaml
clusterServersConfig:
  nodeAddresses:
  - "redis://127.0.0.1:7004"
  - "redis://127.0.0.1:7001"
  - "redis://127.0.0.1:7000"
  readMode: "SLAVE"
  slaveConnectionMinimumIdleSize: 24
  slaveConnectionPoolSize: 64
  masterConnectionMinimumIdleSize: 24
  masterConnectionPoolSize: 64
```

`ReadMode`로 설정이 가능한 값은:

- SLAVE - salve(replica)에서 읽음. 불가능할 경우, master 노드에서 읽음. (default)
- MASTER - master에서만 읽음.
- MASTER_SLAVE - master와 slave(replica) 모두에서 읽음.


# Jedis

Jedis는 replica(slave)에서 읽어오는 기능이 안 되는 것으로 알고 있다.


# 참고

- [ReadFrom settings - Lettuce](https://github.com/lettuce-io/lettuce-core/wiki/ReadFrom-Settings)
- [MasterReplica - Lettuce](https://github.com/lettuce-io/lettuce-core/wiki/Master-Replica)
- [Redis Master-Slave Replication - Redisson](https://redisson.org/glossary/redis-master-slave-replication.html)
- [Master slave mode - Redisson](https://github.com/redisson/redisson/wiki/2.-Configuration#28-master-slave-mode)
