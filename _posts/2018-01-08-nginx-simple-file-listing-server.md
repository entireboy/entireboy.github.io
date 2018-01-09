---
layout: post
title:  "[nginx] 간단 파일리스팅 서버"
date:   2018-01-08 21:18:00 +0900
published: true
categories: [ nginx ]
tags: [ nginx, simple, web server, file, listing, static, server, file listing, sample ]
---

[nginx](https://nginx.org/)로 간단하게 파일 리스팅 서버를 만들 수 있다. static 리소스를 담고 서빙하는 용도나 빌드 후 릴리즈 파일들을 넣는 용도로 사용하면 좋을듯..

```
user nobody;
worker_processes 2;

events {
    worker_connections 1024;
}

http {
    include mime.types;
    default_type application/octet-stream;
    keepalive_timeout 65;

    server {
        location /releases/ {
            root /my/files/location;
            access_log logs/file_list_access_log;
            error_log logs/file_list_error_log;
            autoindex on;
        }
    }
}
```

`nginx.conf` 파일 내용이다.

nginx를 시작하고 `http://127.0.0.1/releases/`로 접속하면 `/my/files/location/releases/` 경로 아래의 파일들이 주욱 나온다. `location`으로 static 리소스를 정의할 수 있고, 이 path로 접속하면 `root`에 정의된 경로의 파일들을 보여준다. `root`는 말 그대로 root이고, 이 path(`/my/files/location`)에 접속한 url path(`/releases/`)를 붙인 경로의 파일들이 보인다.

`http://127.0.0.1/releases/` url 마지막에 `/`가 붙어 있다. url 마지막의 `/`는 파일이 아닌 폴더를 뜻하고, 이 경우는 폴더 내의 파일을 리스팅하게 된다. 보안 등을 위해 어떤 파일이 있는지 리스팅하고 싶지 않은 경우 `autoindex`라인을 지우거나 `off`시키면 된다.


# 참고

- [Best Practices for Writing Bash Scripts](https://kvz.io/blog/2013/11/21/bash-best-practices/)
