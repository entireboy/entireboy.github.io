---
layout: post
title:  "[shell] ssh 접속 안 될 때 확인할 것들"
date:   2021-01-01 22:18:00 +0900
published: true
categories: [ shell ]
tags: [ shell, ssh, connect, checklist ]
---

까먹을까봐 기록용..

ssh 접속 안 될 때 이런 것들 확인해 보자.


# -v -vvv 옵션

```bash
[~]$ ssh -vvv -i ~/.ssh/my.pem ec2-user@10.xx.yy.zzz
OpenSSH_7.4p1, OpenSSL 1.0.2k-fips  26 Jan 2017
debug1: Reading configuration data /etc/ssh/ssh_config
debug1: /etc/ssh/ssh_config line 58: Applying options for *
debug2: resolving "10.xx.yy.zzz" port 22
debug2: ssh_connect_direct: needpriv 0
debug1: Connecting to 10.xx.yy.zzz [10.xx.yy.zzz] port 22.
debug1: Connection established.
debug1: key_load_public: No such file or directory
debug1: identity file /home/th.deng/.ssh/minorbiz.pem type -1
debug1: key_load_public: No such file or directory
debug1: identity file /home/th.deng/.ssh/minorbiz.pem-cert type -1
debug1: Enabling compatibility mode for protocol 2.0
debug1: Local version string SSH-2.0-OpenSSH_7.4
ssh_exchange_identification: read: Connection reset by peer
```

`-v`는 3번 까지 중첩할 수 있다.

> -v
> Verbose mode.
> Causes ssh to print debugging messages about its progress. This is helpful in debugging connection, authentication, and configuration problems. Multiple -v options increase the verbosity. The maximum is 3.
