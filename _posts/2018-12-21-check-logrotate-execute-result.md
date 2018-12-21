---
layout: post
title:  "[logrotate] 실행 결과 확인"
date:   2018-12-21 22:18:00 +0900
published: true
categories: [ logrotate ]
tags: [ logrotate, linux, system, log, rotate, execute, execution, result, state, util, utility, config, configuration, check ]
---

logrotate를 [디버깅으로 실행]({{ site.baseurl }}{% post_url 2018-01-30-logrotate-debugging %})해서 어떤 파일이 어떻게 rotate될지 예상을 할 수는 있지만, 오늘 언제 어떻게 실행됐는지 결과를 확인하기는 어렵다.


# State file

logrotate의 state 파일을 확인하면, (아주 간단한 내용이긴 하지만) 실행 대상 별로 가장 최근에 언제 실행됐는지 정도를 확인할 수 있다.

```bash
$ cat /var/lib/logrotate/logrotate.status
logrotate state -- version 2
"/usr/local/nginx/logs/error_log" 2018-12-21-3:34:2
"/var/log/yum.log" 2018-1-1-3:6:1
"/usr/local/nginx/logs/front_error_log" 2018-12-20-3:42:1
"/usr/local/nginx/logs/front_access_log" 2018-12-21-3:34:2
"/var/tomcat/*/logs/catalina.out" 2018-12-21-3:0:0
"/var/log/spooler" 2018-12-17-3:11:1
"/var/log/maillog" 2018-12-17-3:11:1
```

state 파일 위치는 default가 `/var/lib/logrotate/logrotate.status`이지만, 아래처럼 `-s`나 `--state` 옵션으로 변경할 수 있다.

```bash
/usr/sbin/logrotate -s /var/lib/logrotate/logrotate.status /etc/logrotate.conf
```

# Logging of logrotate

그리고 logrotate는 자체적으로 로그를 생성하지 않는다. 만일 로그를 남기고 싶다면 logrotate가 등록된 cron 스크립트 파일을 변경해서 남기면 된다. logrotate를 설치하면, 보통 daily cron에 실행 스크립트가 생긴다. 요 cron 스크립트를 잘 수정하면 좋을 것 같다.

```bash
$ sudo cat /etc/cron.daily/logrotate
#!/bin/sh

/usr/sbin/logrotate /etc/logrotate.conf
EXITVALUE=$?
if [ $EXITVALUE != 0 ]; then
    /usr/bin/logger -t logrotate "ALERT exited abnormally with [$EXITVALUE]"
fi
exit 0
```


# 참고

- [logrotate - ubuntu man page](http://manpages.ubuntu.com/manpages/xenial/man8/logrotate.8.html)
- [How does logrotate exactly handle “daily”?](https://serverfault.com/questions/198203/how-does-logrotate-exactly-handle-daily)
- [Sample logrotate configuration and troubleshooting](https://support.rackspace.com/how-to/sample-logrotate-configuration-and-troubleshooting/)
- [Where does logrotate save its own log?](https://serverfault.com/questions/381081/where-does-logrotate-save-its-own-log)
