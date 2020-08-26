---
layout: post
title:  "[nginx] 특정 request의 access log 변경하기 (개인정보 마스킹 등)"
date:   2020-08-26 21:18:00 +0900
published: true
categories: [ nginx ]
tags: [ nginx, replace, access log, url, request, param, query, log, logging, modify ]
---

요청 받는 URL의 path나 request param 으로 개인정보가 들어올 때가 있다. 이럴 때 nginx의 access log로 이 개인정보들이 남는 경우가 있는데, 개인정보이기 때문에 지우거나 마스킹처리가 필요한 변경하거나 제거해야 한다.

`nginx.conf`에 로깅 설정을 보면 이런 형태로 많이 되어 있다.

```
http {
  log_format main '$remote_addr - $remote_user [$time_local] '
                  '"$request" $status $body_bytes_sent '
                  '"$http_referer" "$http_user_agent" $request_time '
                  '$upstream_response_time "$upstream_addr" "$http_x_forwarded_for" '
                  '$upstream_cache_status';
}
```

`GET` 요청의 query param 의 정보를 마스킹 하고 싶을 때는 `$request` 를 변환해서 저장하면 된다.

```
http {
  log_format main '$remote_addr - $remote_user [$time_local] '
                  '"$replaced_request" $status $body_bytes_sent '
                  '"$http_referer" "$http_user_agent" $request_time '
                  '$upstream_response_time "$upstream_addr" "$http_x_forwarded_for" '
                  '$upstream_cache_status';
  server {
    set $replaced_request $request;
    if ($replaced_request ~ (.*)lat=[^&]*(.*)) {
        set $replaced_request $1lat=****$2;
    }
    if ($replaced_request ~ (.*)lng=[^&]*(.*)) {
        set $replaced_request $1lng=****$2;
    }
  }
}
```

특정 request path 에 대해서 로그를 남기고 싶지 않을 때는 아래처럼 `location` 설정으로 로깅을 끌 수 있다.

```
http {
  server {
    location /dont/wanna/log/accesslog {
      access_log off;
    }
  }
}
```


# 참고

- [https://stackoverflow.com/questions/19265766/how-to-not-log-a-get-request-parameter-in-the-nginx-access-logs](How to not log a get request parameter in the nginx access logs)
- [https://forum.nginx.org/read.php?11,234313,234314](Masking query string password in access log)
