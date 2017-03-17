---
layout: post
title:  "[Play] Application secret key"
date:   2016-12-25 21:15:00 +0900
published: true
categories: [ play ]
tags: [ play, framework, application secret, secret, config, application.conf ]
---

Play([https://playframework.com/](https://playframework.com/))를 production 모드에서 실행하려면 [application secret](http://playframework.com/documentation/latest/ApplicationSecret)을 꼭 설정해 주어야 한다. 이 값을 세션 쿠키나 CSRF 토큰을 만드는 등 여러 암호화 작업에 사용된다고 한다.

application secret을 설정해 주지 않고 실행하면 이런 친절한 설명과 함께 실행되지 않는다.

```bash
[info] a.e.s.Slf4jLogger - Slf4jLogger started
[info] a.r.Remoting - Starting remoting
[info] a.r.Remoting - Remoting started; listening on addresses :[akka.tcp://application@172.19.0.101:2552]
[info] a.c.Cluster(akka://application) - Cluster Node [akka.tcp://application@172.19.0.101:2552] - Starting up…
[info] a.c.Cluster(akka://application) - Cluster Node [akka.tcp://application@172.19.0.101:2552] - Registered cluster JMX MBean [akka:type=Cluster]
[info] a.c.Cluster(akka://application) - Cluster Node [akka.tcp://application@172.19.0.101:2552] - Started up successfully
[error] p.a.l.c.CryptoConfigParser - The application secret has not been set, and we are in prod mode. Your application is not secure.
[error] p.a.l.c.CryptoConfigParser - To set the application secret, please read http://playframework.com/documentation/latest/ApplicationSecret
[error] p.a.l.c.CryptoConfigParser - The application secret has not been set, and we are in prod mode. Your application is not secure.
[error] p.a.l.c.CryptoConfigParser - To set the application secret, please read http://playframework.com/documentation/latest/ApplicationSecret
Oops, cannot start the server.
@72eg7k97n: Configuration error
   at play.api.libs.crypto.CryptoConfigParser.get$lzycompute(Crypto.scala:498)
   at play.api.libs.crypto.CryptoConfigParser.get(Crypto.scala:465)
   at play.api.libs.crypto.CryptoConfigParser.get(Crypto.scala:463)
   at com.google.inject.internal.ProviderInternalFactory.provision(ProviderInternalFactory.java:81)
   at com.google.inject.internal.BoundProviderFactory.provision(BoundProviderFactory.java:72)
   ( .. 생략 .. )
```

간단히 `application.conf` 파일에 아무 값이나 설정해 주면 된다.

```javascript
play.crypto.secret="This is my new super ultra extremely splendid shiny very very very strong and long secret key"
```

이렇게 `application.conf` 파일에 넣어 버리면 VCS에도 업로드 되니, 아래처럼 설정해 주고 실행할 때 마다 system environment variables로 넣어주는게 좋겠다.

```javascript
play.crypto.secret="changeme" // 사실 이 default는 play.jar의 resources.conf 안에 있기 때문에 없어도 된다.
play.crypto.secret=${?APPLICATION_SECRET}
```

내부적으로는 `changeme`라는 값이 play의 `resources.conf` 파일에 default로 되어 있다. `play.api.libs.crypto.CryptoConfigParser` 소스를 열어보면, prod 모드로 실행할 때 `play.crypto.secret` 설정값이 `changeme`로 되어 있으면 에러를 뿜고 죽어버리게 되어 있다.

```scala
val secret = config.getDeprecated[Option[String]]("play.crypto.secret", "application.secret") match {
 case (Some("changeme") | Some(Blank()) | None) if environment.mode == Mode.Prod =>
   logger.error("The application secret has not been set, and we are in prod mode. Your application is not secure.")
   logger.error("To set the application secret, please read http://playframework.com/documentation/latest/ApplicationSecret")
   throw new PlayException("Configuration error", "Application secret not set")
 ( .. 생략 .. )
}
```

# 참고

- [http://playframework.com/documentation/latest/ApplicationSecret](http://playframework.com/documentation/latest/ApplicationSecret)
