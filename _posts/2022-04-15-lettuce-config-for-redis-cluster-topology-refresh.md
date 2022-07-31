---
layout: post
title:  "[Lettuce] Redis cluster 교체를 위한 설정 - topology refresh"
date:   2022-04-15 22:18:00 +0900
published: true
categories: [ redis ]
tags: [ redis, cluster, lettuce, config, topology, refresh, switch ]
---

# Redis cluster

Redis cluster를 사용하면, 처음 seed node에 접속해서 클러스터 구성(topology)을 얻어온다. 각 노드들의 IP와 같은 접속 정보를 가져온다.
그런데, 간혹 노드 교체나 시스템 점검으로 구성이 바뀌면 문제가 발생하기도 한다.
- Topology 정보 갱신(refresh)
- Node 정보가 DNS 형태로 되어 있는 경우 DNS 캐시

Cluster topology가 바뀌면 자동으로 그 정보를 가져올 수 있는 설정을 까먹을까봐 정리..


# Refresh Redis cluster topology

```kotlin
private fun createRedisClientConfiguration(redisProperties: RedisProperties, clientResources: ClientResources) =
        LettuceClientConfiguration.builder()
            .commandTimeout(redisProperties.timeout)
            .readFrom(ReadFrom.REPLICA_PREFERRED)                     // 읽기 명령은 replica에서 우선으로 실행
            .shutdownTimeout(redisProperties.lettuce.shutdownTimeout)
            .clientOptions(getClusterClientOptions(redisProperties))
            .clientResources(clientResources)
            .build()

private fun getClusterClientOptions(redisProperties: RedisProperties): ClientOptions {
    val topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
        .dynamicRefreshSources(true)                                  // default: true
        .enablePeriodicRefresh(redisProperties.lettuce.cluster.refresh.period)
        .enableAllAdaptiveRefreshTriggers()
        .adaptiveRefreshTriggersTimeout(Duration.ofSeconds(30))       // default: 30초
        .build()

    return ClusterClientOptions.builder()
        .autoReconnect(true)
        .publishOnScheduler(true)
        .disconnectedBehavior(ClientOptions.DisconnectedBehavior.DEFAULT)
        .socketOptions(SocketOptions.builder().connectTimeout(redisProperties.connectTimeout).keepAlive(true).build())
        .topologyRefreshOptions(topologyRefreshOptions)
        .timeoutOptions(TimeoutOptions.enabled(redisProperties.timeout))
        .build()
}
```

`topologyRefreshOptions`는 topology가 변경되었을 때 감지하고 변경된 노드에 접속할 수 있도록 하는 설정이다.
- `dynamicRefreshSources` (default: true): true로 설정하면, 발견된 모든 노드로부터 topology 정보를 얻어온다. false로 설정하면, 처음 설정한 seed 노드로부터로만 topology 정보를 얻어온다.
- `enablePeriodicRefresh` (default: 60초): topology 정보가 변경되었는지 감지하는 시간 텀. 노드 교체 등으로 topology 변경이 예정되어 있다면 짧게 주어도 좋을 것 같다.
- `enableAllAdaptiveRefreshTriggers` (default: 사용하지 않음): [RefreshTrigger](https://lettuce.io/core/release/api/io/lettuce/core/cluster/ClusterTopologyRefreshOptions.RefreshTrigger.html)에 enum으로 설정된 모든 refresh 이벤트에 대해 topology 갱신을 실행한다. Redis cluster에서 `MOVED`, `ACK` 같은 refresh trigger 이벤트가 많이 발생하는 경우 계속 topology를 갱신하려고 해서 성능 이슈가 발생할 수가 있다. [enableAdaptiveRefreshTrigger](https://lettuce.io/core/release/api/io/lettuce/core/cluster/ClusterTopologyRefreshOptions.Builder.html#enableAdaptiveRefreshTrigger-io.lettuce.core.cluster.ClusterTopologyRefreshOptions.RefreshTrigger...-)를 이횽해서 원하는 refresh trigger에서만 갱신하도록 설정할 수 있다.
- `adaptiveRefreshTriggersTimeout` (default: 30초): adaptive refresh 주기

```
Adaptive triggers lead to an immediate topology refresh. These refreshes are rate-limited using a timeout since events can happen on a large scale. Adaptive refresh triggers are disabled by default

- from [7.2.1. Cluster-specific options - Lettuce docs](https://lettuce.io/core/5.3.7.RELEASE/reference/index.html#clientoptions.cluster-specific-options)
```


# DNS Cache

Redis cluster의 노드 정보가 DNS로 설정되어 있는 경우, 어플리케이션은 처음 seed 노드에 접속해서 DNS를 받아오게 된다. 이 때, 노드만 교체되고 DNS가 같은 경우 계속 이전 노드의 IP로 접속을 시도하게 된다. 새로 변경된 IP로 접속할 수 있도록 DNS 캐시 시간을 조절해야 한다.

JVM 실행 시 다음 설정이 있는지 확인한다. 설정이 없다면 [기본값은 30초]({{ site.baseurl }}{% post_url 2017-07-31-java-dns-caching-ttl %})이고, `networkaddress.cache.ttl` 설정이 `sun.net.inetaddr.ttl` 보다 우선순위가 높다.

```
-Dsun.net.inetaddr.ttl=10

// 또는

-Dnetworkaddress.cache.ttl=10
```


# 참고

- [5.3.6. Client-options - Lettuce docs](https://lettuce.io/core/5.3.7.RELEASE/reference/index.html#redis-cluster.client-options)
- [7.2.1. Cluster-specific options - Lettuce docs](https://lettuce.io/core/5.3.7.RELEASE/reference/index.html#clientoptions.cluster-specific-options)
- [RefreshTrigger](https://lettuce.io/core/release/api/io/lettuce/core/cluster/ClusterTopologyRefreshOptions.RefreshTrigger.html)
