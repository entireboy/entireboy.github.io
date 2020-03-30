---
layout: post
title:  "[Java] DNS caching TTL - networkaddress.cache.ttl"
date:   2017-07-31 21:18:00 +0900
published: true
categories: [ java ]
tags: [ java, dns, lookup, caching, cache, ttl, network ]
---

도메인 이름으로 DNS lookup을 하면 캐싱을 하게 된다.

캐싱하는 시간은 JDK 구현에 따라 다를 수 있고, security manager를 사용하지 않으면 JDK 6 이상은 [30초를 캐싱][2]하게 된다.([1][1]) security manager 설정을 하게 되면 무한(forever)으로 캐싱을 하고, security manager는 기본적으로 disable 되어 있다. (잘못된 내용 댓글로 알려주신 성호님 고맙습니다.) 캐싱하지 않게 하거나 캐싱할 시간을 지정해 주려면 JVM 실행 시 `networkaddress.cache.ttl` 옵션을 주면 된다.

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
- [InetAddressCachePolicy.java][2]

[1]: http://docs.oracle.com/javase/7/docs/technotes/guides/net/properties.html#nct
[2]: http://hg.openjdk.java.net/jdk8/jdk8/jdk/file/687fd7c7986d/src/share/classes/sun/net/InetAddressCachePolicy.java
