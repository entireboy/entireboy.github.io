---
layout: post
title:  "[logrotate] 정상적으로 rotate되는지 debugging하기"
date:   2018-01-30 22:18:00 +0900
published: true
categories: [ logrotate ]
tags: [ logrotate, linux, system, log, rotate, util, utility, debug, debugging, dry run ]
---

# logrotate

서버의 로그를 (주로 일단위로) 잘라서 관리해 주는 logrotate라는 유틸이 있다. 대부분의 리눅스 시스템에서 패키지 형태로 간단히 설치할 수 있게 배포되고 있다. ([logrotate - ubuntu man page](http://manpages.ubuntu.com/manpages/xenial/man8/logrotate.8.html) 참고)


# Dry run (debugging)

그런데, 하루에 한 번 rotate 되도록 logrotate 설정을 한 다음 정상적으로 도는지 확인하려면 하루를 기다려야 할까?? 설정을 한 다음 바로 돌려보거나 확인할 수 있는 방법은 없을까??
이 때 `-vd` 옵션을 주고 실행해 보면 된다. `-d`, `--debug` 옵션은 디버깅 옵션이고, 실제로 rotate가 실행되지는 않고 그 결과만 보여준다. `-v`, `--verbose` 옵션은 메시지를 화면에 보여준다.

```bash
$ /usr/sbin/logrotate -vd /etc/logrotate.conf
```

위와 같이 실행하면, 어떤 파일이 rotate 대상이고, 어떤 파일은 어떤 이유로 rotate가 되지 않는지 친절히 하나하나 보여준다.


# 참고

- [Understanding logrotate utility](https://support.rackspace.com/how-to/understanding-logrotate-utility/)
- [Sample logrotate configuration and troubleshooting](https://support.rackspace.com/how-to/sample-logrotate-configuration-and-troubleshooting/)
- [logrotate - ubuntu man page](http://manpages.ubuntu.com/manpages/xenial/man8/logrotate.8.html)
