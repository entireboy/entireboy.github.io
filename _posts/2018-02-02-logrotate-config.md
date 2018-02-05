---
layout: post
title:  "[logrotate] 설정"
date:   2018-02-02 22:18:00 +0900
published: true
categories: [ logrotate ]
tags: [ logrotate, linux, system, log, rotate, util, utility, config, configuration ]
---

# logrotate

서버의 로그를 (주로 일단위로) 잘라서 관리해 주는 logrotate라는 유틸이 있다. 대부분의 리눅스 시스템에서 패키지 형태로 간단히 설치할 수 있게 배포되고 있다. ([logrotate - ubuntu man page](http://manpages.ubuntu.com/manpages/xenial/man8/logrotate.8.html) 참고)


# Config

logrotate의 설정파일은 다음 파일에 있다.

- /etc/logrotate.conf: logrotate의 전반적인 설정
- /etc/logrotate.d/*: rotate를 돌릴 각 프로그램의 설정

```bash
$ cat /etc/logrotate.d/jenkins
/var/log/jenkins/jenkins.log /var/log/jenkins/access_log {
    compress
    dateext
    maxage 365
    rotate 99
    size=+4096k
    notifempty
    missingok
    create 644
    copytruncate
}
```


# Cron

주기적으로 실행되는 원리는 `/etc/cron.[hourly, daily, weekly, monthly]` 경로에 있다.

logrotate를 설치하면, 일반적으로 `/etc/cron.daily/logrotate` 파일이 생성된다. `/etc/cron.daily` 안에 있는 스크립트는 매일 하루에 한 번 실행되는 설정이다. 만일 매시간 마다 logrotate를 실행시키고 싶다면, `/etc/cron.hourly`안에 복사해 넣으면 된다.


# 참고

- [logrotate debugging(dry run)]({{ site.baseurl }}{% post_url 2018-01-30-logrotate-debugging %})
- [Understanding logrotate utility](https://support.rackspace.com/how-to/understanding-logrotate-utility/)
- [Sample logrotate configuration and troubleshooting](https://support.rackspace.com/how-to/sample-logrotate-configuration-and-troubleshooting/)
- [logrotate - ubuntu man page](http://manpages.ubuntu.com/manpages/xenial/man8/logrotate.8.html)
