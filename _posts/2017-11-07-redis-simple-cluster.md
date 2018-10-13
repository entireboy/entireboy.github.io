---
layout: post
title:  "[Redis] Redis cluster 구성하기"
date:   2017-11-07 21:18:00 +0900
published: true
categories: [ redis ]
tags: [ redis, cluster, tutorial, sample, simple, config, add, remove, master, slave, node ]
---

초간단하게 심플한 Redis cluster 만들기.. (까먹기 전에 기록기록)

이 샘플에서는 동일한 서버에 3개 노드로 구성된 클러스터를 만든다. slave는 없고, master 3대로만 구성된 클러스터이다.


# Cluster 노드 설정

각 노드는 7001, 7002, 7003번 port를 사용할 것이다. 알아보기 편하게 `node01`은 `7001` 포트, `node02`는 `7002` 포트 형태로 구성한다. `redis.conf`를 복사해서 아래와 같이 설정을 수정하고 `redis-cluster01.conf`로 저장한다.

```
port 7001
cluster-enabled yes
cluster-node-timeout 5000
pidfile /var/run/redis_7001.pid
dbfilename dump-cluster01.rdb
cluster-config-file nodes-7001.conf
```

7002, 7003 포트 설정도 각각 수정해서 별도 파일로 저장한다.


# Redis instance 실행

위에서 저장한 각 `redis.conf` 파일을 설정으로 Redis 서버를 띄운다.

```bash
$ redis-server ./redis-cluster01.conf &
$ redis-server ./redis-cluster02.conf &
$ redis-server ./redis-cluster03.conf &
```


# Cluster 만들기

아래 명령으로 각자 띄워진 Redis를 클러스터로 묶는다. redis-trib.rb 명령은 `$REDIS_HOME/bin`에 있는데, 간혹 distribution 마다 없는 경우가 있다. 만일 없다면, <http://download.redis.io/redis-stable/src/redis-trib.rb> 에서 다운받으면 된다.

```bash
$ redis-trib.rb create --replicas 0 127.0.0.1:7001 127.0.0.1:7002 127.0.0.1:7003
```

클러스터로 묶을 Redis 서버를 나열하면 되고, `--replicas 0` 옵션으로 각 노드 마다 몇 대의 slave가 붙을지 결정할 수 있다. 여기서는 slave 없이 클러스터를 생성하기 때문에 `0`으로 줬다.

클러스터가 정상적으로 생성됐다면, 아래 명령으로 각 노드를 확인할 수 있다. 클러스터 중 아무 노드에 접속해서 `cluster nodes`를 확인하면 된다. `myself`로 현재 접속한 노드를 확인할 수 있다.

```bash
$ redis-cli -h 127.0.0.1 -p 7001 cluster nodes
3aef75f4eb38fb835b5663b211f41eac9640b16d 10.10.10.10:7002@17002 master - 0 1509976819451 2 connected 5461-10922
36b58661b1fa599fdf45c41c027c09d5e360971f 10.10.10.10:7001@17001 myself,master - 0 1509976818000 1 connected 0-5460
379c8c7ae7156f3cd1a669cb9fd53b5855ff7ece 10.10.10.10:7003@17003 master - 0 1509976820452 3 connected 10923-16383
```

각 노드 설정의 마지막에 보면, 노드 마다 담당(?)하고 있는 hash 값 영역을 알 수 있다. Redis 클러스터는 16384개(16k)의 해시 슬롯을 노드별로 나눠서 가지고 있다. 위의 샘플에서는 01번 노드는 0 부터 5460 까지, 02번 노드는 5461 부터 10922 까지, 03번 노드는 10923 부터 16383 까지 담겨 있다.

{% include google-ad-content %}


# Cluster 노드 추가하기

운영하다 보면 노드를 추가할 때가 있다.

```bash
$ redis-trib.rb add-node 127.0.0.1:7007 127.0.0.1:7001
```

첫번째 전달인자는 새로 추가할 노드(127.0.0.1:7007)이고, 두번째 전달인자는 기존 클러스터 노드(127.0.0.1:7001)이다.

master 대신 slave로 추가할 때는 `--slave` 옵션을 주면 된다.

```bash
$ redis-trib.rb add-node --slave 127.0.0.1:7007 127.0.0.1:7001
```

위처럼 slave를 추가하면 replica가 적은 master에 자동으로 붙는다. 내가 원하는 master에 slave로 붙이려면 아래처럼 `--master-id`를 주면 된다. master-id는 `cluster nodes` 명령으로 확인할 수 있다.

```bash
$ redis-trib.rb add-node --slave --master-id 3aef75f4eb38fb835b5663b211f41eac9640b16d 127.0.0.1:7007 127.0.0.1:7001
```


# Cluster 노드 제거하기

노드를 제거할 때는 `del-node`를 사용하면 된다.

```bash
$ redis-trib del-node 127.0.0.1:7001 3aef75f4eb38fb835b5663b211f41eac9640b16d
```

첫번째 전달인자(127.0.0.1:7001)는 노드를 제거할 클러스터 중 아무 노드나 주고, 마지막 전달인자로 제거할 노드 id를 주면 된다.


# 참고

- [Redis cluster tutorial - Redis documentation](https://redis.io/topics/cluster-tutorial)
- [redis-trib.rb](http://download.redis.io/redis-stable/src/redis-trib.rb)
