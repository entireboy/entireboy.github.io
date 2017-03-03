---
layout: post
title:  "[DNS] SRV Record"
date:   2017-01-28 10:01:00 +0900
categories: [ dns, srv ]
tags: [ dns, srv, srv_record, type, a_record, lookup, hostname, nslookup ]
---

보통 도메인 이름으로 IP를 lookup 하는 용도로 DNS를 많이 사용한다. IPv4를 lookup할 때는 [A Record](https://en.wikipedia.org/wiki/List_of_DNS_record_types#A) 형태로 저장된다. IPv6는 [AAAA Record](https://en.wikipedia.org/wiki/List_of_DNS_record_types#AAAA) 형태로 저장된다.

hostname을 조금 더 확장해서 동일한 hostname에 있는 여러 서비스의 하나하나를 lookup할 수 있는 용도로 [SRV Record](https://en.wikipedia.org/wiki/SRV_record)를 사용할 수 있다.

```
_service._proto.name. TTL class SRV priority weight port target.
```

- service: 서비스 이름
- proto: protocol (TCP, UDP 등)
- name: 도메인 이름
- TTL: DNS에서 살아 있을 기간 Time to Live
- class: DNS class로 항상 IN
- priority: 우선순위. 작은 숫자가 더 높은 우선순위
- weight: 동일한 우선순위를 가졌을 때 비교할 가중치
- port: 서비스 port
- target: 서비스에 할당한 hostname

A Record에서 `_service._proto` 부분이 추가로 늘었다고 생각하면 조금 간단하다.

```
# _service._proto.name.  TTL   class SRV priority weight port target.
_sip._tcp.example.com.   86400 IN    SRV 10       60     5060 bigbox.example.com.
_sip._tcp.example.com.   86400 IN    SRV 10       20     5060 smallbox1.example.com.
_sip._tcp.example.com.   86400 IN    SRV 10       20     5060 smallbox2.example.com.
_sip._tcp.example.com.   86400 IN    SRV 20       0      5060 backupbox.example.com.
```

아래 커맨드들로 lookup 할 수 있다.

```bash
$ dig _sip._tcp.example.com SRV
$ host -t SRV _sip._tcp.example.com
$ nslookup -querytype=srv _sip._tcp.example.com
$ nslookup
> set querytype=srv
> _sip._tcp.example.com
```

# 참고
- [https://en.wikipedia.org/wiki/SRV_record](https://en.wikipedia.org/wiki/SRV_record)
- [https://tools.ietf.org/html/rfc2782](https://tools.ietf.org/html/rfc2782)
- [https://en.wikipedia.org/wiki/List_of_DNS_record_types#A](https://en.wikipedia.org/wiki/List_of_DNS_record_types#A)
