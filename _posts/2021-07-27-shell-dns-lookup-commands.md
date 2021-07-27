---
layout: post
title:  "[Shell] DNS lookup 커맨드"
date:   2021-07-27 22:18:00 +0900
published: true
categories: [ shell ]
tags: [ shell, dns, lookup, command ]
---

# nslookup

```bash
$ nslookup daum.net
Server:		8.8.8.8
Address:	8.8.8.8#53

Non-authoritative answer:
Name:	daum.net
Address: 203.133.167.16
Name:	daum.net
Address: 211.231.99.17
Name:	daum.net
Address: 203.133.167.81
Name:	daum.net
Address: 211.231.99.80
```


# dig

## 짧게

```bash
$ dig +short daum.net
203.133.167.16
211.231.99.17
203.133.167.81
211.231.99.80
```

## 자세하게

```bash
$ dig daum.net

; <<>> DiG 9.10.6 <<>> daum.net
;; global options: +cmd
;; Got answer:
;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 37676
;; flags: qr rd ra; QUERY: 1, ANSWER: 4, AUTHORITY: 0, ADDITIONAL: 1

;; OPT PSEUDOSECTION:
; EDNS: version: 0, flags:; udp: 512
;; QUESTION SECTION:
;daum.net.			IN	A

;; ANSWER SECTION:
daum.net.		247	IN	A	203.133.167.16
daum.net.		247	IN	A	203.133.167.81
daum.net.		247	IN	A	211.231.99.17
daum.net.		247	IN	A	211.231.99.80

;; Query time: 77 msec
;; SERVER: 8.8.8.8#53(8.8.8.8)
;; WHEN: Tue Jul 27 17:37:14 KST 2021
;; MSG SIZE  rcvd: 101
```
