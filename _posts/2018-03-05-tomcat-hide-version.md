---
layout: post
title:  "[Tomcat] 버전 안 보이게 숨기기"
date:   2018-03-05 22:18:00 +0900
published: true
categories: [ tomcat ]
tags: [ tomcat, was, hide, cover, version ]
---

WAS의 버전을 확인해서 해당 버전의 취약점을 공격하는 경우가 있다. 물론, 이런 공격이 WAS뿐만은 아니다.

Tomcat 같은 경우 기본 404 페이지 같은 곳에 버전이 명시된다.

```bash
$ curl -i -k http://my.host.com/111
HTTP/1.1 404 Not Found
Content-Type: text/html;charset=utf-8
Content-Length: 957
Connection: close
Vary: Accept-Encoding
Content-Language: en

<html><head><title>Apache Tomcat/7.0.62 - Error report</title><style>
<!--H1 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:22px;}
H2 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:16px;}
H3 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:14px;}
BODY {font-family:Tahoma,Arial,sans-serif;color:black;background-color:white;}
B {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;}
P {font-family:Tahoma,Arial,sans-serif;background:white;color:black;font-size:12px;}
A {color : black;}A.name {color : black;}HR {color : #525D76;}--></style></head>

<body>
<h1>HTTP Status 404 - /111</h1><HR size="1" noshade="noshade"><p><b>type</b> Status report</p>
<p><b>message</b> <u>/111</u></p><p><b>description</b> <u>The requested resource is not available.</u></p>
<HR size="1" noshade="noshade"><h3>Apache Tomcat/7.0.62</h3></body></html>
```

404 페이지를 설정해 주지 않으면 이 페이지가 보이는데, 헤더와 바닥에 버전이 보인다. 공격에 취약할 수 있으니 가능하면 404, 5xx 등의 페이지를 만들어 주자. 하지만 그게 어려운 상황이라면 아래처럼 설정 파일을 생성해 주면, 설정 내용으로 버전 정보를 숨길 수 있다.

```bash
$ cat $CATALINA_HOME/lib/org/apache/catalina/util/ServerInfo.properties
server.info=Apache
```

`ServerInfo.properties` 파일을 생성해서 내용을 넣어주면 버전정보 `Apache Tomcat/7.0.62`가 `Apache`로 바뀐 것을 볼 수 있다.

```bash
$ curl -i -k http://my.host.com/111
HTTP/1.1 404 Not Found
Content-Type: text/html;charset=utf-8
Content-Length: 929
Connection: close
Vary: Accept-Encoding
Content-Language: en

<html><head><title>Apache - Error report</title><style>
<!--H1 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:22px;}
H2 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:16px;}
H3 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:14px;}
BODY {font-family:Tahoma,Arial,sans-serif;color:black;background-color:white;}
B {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;}
P {font-family:Tahoma,Arial,sans-serif;background:white;color:black;font-size:12px;}
A {color : black;}A.name {color : black;}HR {color : #525D76;}--></style> </head>

<body>
<h1>HTTP Status 404 - /111</h1><HR size="1" noshade="noshade"><p><b>type</b> Status report</p>
<p><b>message</b> <u>/111</u></p><p><b>description</b> <u>The requested resource is not available.</u></p>
<HR size="1" noshade="noshade"><h3>Apache</h3></body></html>
```


# 참고

- [Error Handling In Playbooks](http://docs.ansible.com/ansible/latest/playbooks_error_handling.html)
- [Security Considerations - Tomcat Documentation](http://tomcat.apache.org/tomcat-8.5-doc/security-howto.html)
