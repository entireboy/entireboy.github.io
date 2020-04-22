---
layout: post
title:  "[nginx] 413 Request Entity Too Large 오류"
date:   2020-04-21 21:18:00 +0900
published: true
categories: [ nginx ]
tags: [ nginx, request, too large, file, upload, size, http, proxy ]
---

[nginx](https://nginx.org/)로 reverse proxy 를 사용할 때, 용량이 큰 파일을 업로드하면 `413 Request Entity Too Large` 라는 메시지를 볼 수 있다.

{% include image.html file='/assets/img/2020-04-21-nginx-413-request-entity-too-large' alt='413 Request Entity Too Large' %}

`client_max_body_size` 설정 때문이고, 너무 큰 사이즈의 request를 보내지 못 하도록 제한을 걸 수 있다. 기본값은 1MB이다. request의 `Content-Length` 헤더값이 여기 설정된 값을 넘을 수 없다. `POST`나 `PUT` 등의 request 사이즈 제한을 할 수도 있지만, 보통 악의적으로 큰 용량의 파일을 업로드해서 디스크를 가득 채우는 경우를 방지하는데 사용되지 않을까 싶다.

`nginx.conf` 파일에서 `http`, `server`, `location`에 설정이 가능하다.

```
http {
    client_max_body_size 5M;

    ...
}
```

설정을 해주지 않으면 기본값은 `1m`이고, 제한을 두지 않으려면 `0`으로 설정하면 된다.

```
Syntax:	client_max_body_size size;
Default:
client_max_body_size 1m;
Context:	http, server, location
```

`nginx.conf` 파일을 수정하고, 재시작해주면 끗

```bash
$ sudo service nginx reload
```


# 참고

- [client_max_body_size - Module ngx_http_core_module](http://nginx.org/en/docs/http/ngx_http_core_module.html#client_max_body_size)
- [Nginx: 413 – Request Entity Too Large Error and Solution](https://www.cyberciti.biz/faq/linux-unix-bsd-nginx-413-request-entity-too-large/)
- [413 Payload Too Large - MDN](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/413)
