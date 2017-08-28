---
layout: post
title:  "[Java] DNS caching TTL - networkaddress.cache.ttl"
date:   2017-07-31 21:18:00 +0900
published: true
categories: [ java ]
tags: [ java, dns, lookup, caching, cache, ttl, network ]
---

도메인 이름으로 DNS lookup을 하면 [JVM이 죽을 때 까지 캐싱][1]하게 된다. 캐싱하지 않게 하거나 캐싱할 시간을 지정해 주려면 JVM 실행 시 `networkaddress.cache.ttl` 옵션을 주면 된다.

|Value|Meaning|
|:---:|-------|
|  -1 |cache forever (**default**)|
|   0 |no cache|
| > 0 |캐시할 시간 (초)|

반대로 DNS lookup이 실패했을 때 한동안 시도하지 않도록 캐싱할 수 있다. `networkaddress.cache.negative.ttl`

|Value|Meaning|
|:---:|-------|
|  -1 |cache forever|
|   0 |no cache|
| > 0 |캐시할 시간 (초), (**default: 10**) |

# 참고

- [Networking Properties - Java SE Document][1]
- [Java VM 의 DNS caching TTL](https://www.lesstif.com/pages/viewpage.action?pageId=17105897)

[1]: http://docs.oracle.com/javase/7/docs/technotes/guides/net/properties.html#nct
