---
layout: post
title:  "[Linux] TCP 소켓 TIME_WAIT 시간 별 리포트"
date:   2018-05-01 21:18:00 +0900
published: true
categories: [ linux ]
tags: [ linux, system, report, collect, tcp, socket, port, time_wait, network, keep-alive ]
---

API 통신을 많이 하다 보면 소켓이 부족해서 더 이상 접속하지 못 하는 경우가 있다. A서버(웹)에서 B서버(API)로의 호출이 많을 때 서버 호출이 끝난 다음 `FIN`에 대한 `ACK`를 받지 못 하면 A서버의 로컬포트는 `TIME_WAIT` 상태로 대기한다.

{% include image.html file='/assets/img/2018-05-01-linux-tcp-socket-timewait-report.jpg' alt='TIME_WAIT' %}

`ACK`를 받지 못 하면 (default) 2분 정도 대기를 하는데, 로컬 포트를 만들 수 있는 개수가 한정되어 있기 때문에 가득 차면 더 이상 API 호출을 할 수 없는 상태가 될 수도 있다.

그렇다면 이렇게 API 호출이 안 되는 현상이 발생했을 때, 이 `TIME_WAIT`으로 로컬포트가 부족한 상태인지 체크하는 방법은 어떻게 될까?? `sar -n SOCK` 명령을 실행하면 시간별로 체크해서 수집해둔 소켓 상태를 볼 수 있다. (`sar` 명령에는 소켓정보 뿐만 아니라 시스템의 여러 정보를 수집하고 볼수 있다. (collect and report) - [sar(1) - Linux man page](https://linux.die.net/man/1/sar))

```bash
$ sar -n SOCK
Linux 3.10.0-693.21.1.el7.x86_64 (my.server.host) 	2018년 05월 03일 	_x86_64_	(2 CPU)
14시 24분 01초    totsck    tcpsck    udpsck    rawsck   ip-frag    tcp-tw
14시 25분 01초       113        10         7         0         0         1
14시 26분 01초       113        10         7         0         0         1
14시 27분 01초       113        10         7         0         0         0
14시 28분 01초       113        10         7         0         0         2
14시 29분 01초       113        10         7         0         0         0
14시 30분 01초       117        11         7         0         0         1
14시 31분 01초       117        11         7         0         0         1
```

`tcp-tw`이 `TIME_WAIT`의 개수이다. 이 개수가 로컬 포트로 사용할 수 있는 범위 개수에 근접했는지 체크하면 된다. 아래 명령 등으로 확인할 수 있고, 이 서버는 28232(= 60999 - 32768 + 1) 만큼 사용할 수 있다.

```bash
$ cat /proc/sys/net/ipv4/ip_local_port_range
32768	60999
$ sysctl net.ipv4.ip_local_port_range
net.ipv4.ip_local_port_range = 32768	60999
```


# 해결 방법

이렇게 로컬포트 고갈이 심한 경우는, 간단하게는 `keep-alive`를 켜서 포트를 재사용하는 방법이 가장 좋다. `TIME_WAIT` 시간이나 로컬포트 개수를 늘리는 방법도 써봤지만, 그래도 호출이 많으면 가득 차는건 어쩔 수 없다.


# 참고

- [CLOSE_WAIT & TIME_WAIT 최종 분석](http://tech.kakao.com/2016/04/21/closewait-timewait/)
- [sar(1) - Linux man page](https://linux.die.net/man/1/sar)
- [TIME_WAIT 상태란 무엇인가 - likejazz](http://docs.likejazz.com/time-wait/)
