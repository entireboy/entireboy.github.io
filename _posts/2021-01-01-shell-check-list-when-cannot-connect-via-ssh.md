---
layout: post
title:  "[shell] ssh 접속 안 될 때 확인할 것들"
date:   2021-01-01 22:18:00 +0900
published: true
categories: [ shell ]
tags: [ shell, ssh, connect, checklist ]
---

까먹을까봐 기록용..


# EC2에 EFS 붙이기

`{EFS_ID}`는 EFS console([https://console.aws.amazon.com/efs/](https://console.aws.amazon.com/efs/))에서 확인할 수 있다.

```bash
$ mkdir {PATH_TO_MOUNT}
$ sudo mount -t efs -o tls {EFS_ID}:/ {PATH_TO_MOUNT}
```

efs의 `/` 경로를 `/var/lib/jenkins` 경로에 마운트(마운트할 경로는 미리 만들어둬야 한다.)

```bash
$ mkdir -p /var/lib/jenkins
$ sudo mount -t efs -o tls fs-123abc45:/ /var/lib/jenkins
```

# EC2에서 EFS 제거하기

```bash
$ sudo umount {PATH_TO_MOUNT}
$ sudo umount /var/lib/jenkins
```
