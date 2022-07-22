---
layout: post
title:  "[Shell] PS 커맨드 화면에서 짤릴 때"
date:   2022-07-22 22:18:00 +0900
published: true
categories: [ shell ]
tags: [ shell, ps, command, wrap, line ]
---

ps 커맨드 결과가 너무 길어서 화면을 벗어나 짤릴 때가 있다.
(아래 커맨드는 docker로 kafka 컨테이너를 하나 띄웠는데, 길고 긴 classpath와 옵션으로 짤린 내용이다)

```bash
$ ps -ef
PID   USER     TIME  COMMAND
    1 root      0:18 /usr/lib/jvm/zulu8-ca/bin/java -Xmx1G -Xms1G -server -XX:+UseG1GC -XX:MaxGCPau
  511 root      0:00 ps -ef
```

쉽게 해결하는 방법은 `echo`와 함께 사용하기

```bash
$ echo "$(ps -ef)"
$ echo `ps -ef`
$ echo "$(ps aux)"
```

결과는 이렇게.. 아아아 길다아아아아!!

```bash
$ echo "$(ps -ef)"
PID   USER     TIME  COMMAND
    1 root      0:19 /usr/lib/jvm/zulu8-ca/bin/java -Xmx1G -Xms1G -server -XX:+UseG1GC -XX:MaxGCPauseMillis=20 -XX:InitiatingHeapOccupancyPercent=35 -XX:+ExplicitGCInvokesConcurrent -XX:MaxInlineLevel=15 -Djava.awt.headless=true -Xloggc:/opt/kafka/bin/../logs/kafkaServer-gc.log -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dkafka.logs.dir=/opt/kafka/bin/../logs -Dlog4j.configuration=file:/opt/kafka/bin/../config/log4j.properties -cp /opt/kafka/bin/../libs/activation-1.1.1.jar:/opt/kafka/bin/../libs/aopalliance-repackaged-2.6.1.jar:/opt/kafka/bin/../libs/argparse4j-0.7.0.jar:/opt/kafka/bin/../libs/audience-annotations-0.5.0.jar:/opt/kafka/bin/../libs/commons-cli-1.4.jar:/opt/kafka/bin/../libs/commons-lang3-3.8.1.jar:/opt/kafka/bin/../libs/connect-api-2.7.1.jar:/opt/kafka/bin/../libs/connect-basic-auth-extension-2.7.1.jar:/opt/kafka/bin/../libs/connect-file-2.7.1.jar:/opt/kafka/bin/../libs/connect-json-2.7.1.jar:/opt/kafka/bin/../libs/connect-mirror-2.7.1.jar:/opt/kafka/bin/../libs/connect-mirror-client-2.7.1.jar:/opt/kafka/bin/../libs/connect-runtime-2.7.1.jar:/opt/kafka/bin/../libs/connect-transforms-2.7.1.jar:/opt/kafka/bin/../libs/hk2-api-2.6.1.jar:/opt/kafka/bin/../libs/hk2-locator-2.6.1.jar:/opt/kafka/bin/../libs/hk2-utils-2.6.1.jar:/opt/kafka/bin/../libs/jackson-annotations-2.10.5.jar:/opt/kafka/bin/../libs/jackson-core-2.10.5.jar:/opt/kafka/bin/../libs/jackson-databind-2.10.5.1.jar:/opt/kafka/bin/../libs/jackson-dataformat-csv-2.10.5.jar:/opt/kafka/bin/../libs/jackson-datatype-jdk8-2.10.5.jar:/opt/kafka/bin/../libs/jackson-jaxrs-base-2.10.5.jar:/opt/kafka/bin/../libs/jackson-jaxrs-json-provider-2.10.5.jar:/opt/kafka/bin/../libs/jackson-module-jaxb-annotations-2.10.5.jar:/opt/kafka/bin/../libs/jackson-module-paranamer-2.10.5.jar:/opt/kafka/bin/../libs/jackson-module-scala_2.13-2.10.
  511 root      0:00 ps -ef
  ```
