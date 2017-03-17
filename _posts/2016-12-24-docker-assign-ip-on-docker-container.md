---
layout: post
title:  "[Docker] Assign IP on Docker Container"
date:   2016-12-24 01:57:00 +0900
published: true
categories: [ docker, ip ]
tags: [ docker, assign, ip, container, network ]
---

Docker container에 특정 IP를 할당해 줘야 하는 경우가 있다. docker run 실행 시 `--ip` 옵션으로 IP를 주면 된다.

```bash
$ docker run -it --rm --ip 10.10.10.10 my-image:v1
docker: Error response from daemon: User specified IP address is supported on user defined networks only.
```

역시. 한번에 되면 재미 없지.. IP를 할당해 주려면 container를 사용자 지정 네트웍에서 실행해 줘야 한다. 우선, 네트웍을 만들자.

`mynet` 이라는 이름으로 네트웍을 하나 만들자. `docker network create` 실행 시 `-d` 옵션으로 네트웍 드라이버를 선택할 수 있고, default는 `bridge` 이다. ([Docker container networking](https://docs.docker.com/engine/userguide/networking/) 참고)

```bash
$ docker network create --gateway 172.19.0.1 --subnet 172.19.0.0/21 mynet
01674eb504f201c4c866ce9232455c9dc13e692b8c3a4d9a0323fd5bf849253b
$ docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
74b9c320da82        bridge              bridge              local
4f2323a7701c        host                host                local
01674eb504f2        mynet               bridge              local
706556bf613d        none                null                local
$ docker network inspect mynet
[
   {
       "Name": "mynet",
       "Id": "01674eb504f201c4c866ce9232455c9dc13e692b8c3a4d9a0323fd5bf849253b",
       "Scope": "local",
       "Driver": "bridge",
       "EnableIPv6": false,
       "IPAM": {
           "Driver": "default",
           "Options": {},
           "Config": [
               {
                   "Subnet": "172.19.0.0/21",
                   "Gateway": "172.19.0.1"
               }
           ]
       },
       "Internal": false,
       "Containers": {},
       "Options": {},
       "Labels": {}
   }
]
```

이제 `--network` 옵션으로 새로 생성한 `mynet` 네트웍에 컨테이너를 만들고 돌려주자. 네트웍 대역에 맞춰서 IP도 주면 오케!!

```bash
$ docker run -it --rm --network mynet --ip 172.19.0.101 my-image:v1
```

# 참고

- [https://docs.docker.com/engine/reference/commandline/run/](https://docs.docker.com/engine/reference/commandline/run/)
- [https://docs.docker.com/engine/reference/commandline/network_create/](https://docs.docker.com/engine/reference/commandline/network_create/)
- [https://docs.docker.com/engine/userguide/networking/](https://docs.docker.com/engine/userguide/networking/)
- [http://stackoverflow.com/questions/27937185/assign-static-ip-to-docker-container](http://stackoverflow.com/questions/27937185/assign-static-ip-to-docker-container)
