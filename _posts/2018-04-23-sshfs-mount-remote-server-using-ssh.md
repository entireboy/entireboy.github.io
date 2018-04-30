---
layout: post
title:  "[Linux] sshfs로 다른 시스템 마운트(mount)하기"
date:   2018-04-23 22:18:00 +0900
published: true
categories: [ linux ]
tags: [ linux, sshfs, ssh, sftp, fuse, mount ]
---

# 필요

특정 경로를 다른 서버로 연결(mount)하고 싶은 경우가 있다. 예를 들면, 백업해 둘 경로를 백업서버로 연결해서, 백업 경로에 파일을 넣으면 바로 백업서버에 저장이 되도록 할 수 있다.

```bash
[server1] /my/backups/jenkins -> [백업 서버] /backups/server1/jenkins
[server1] /my/backups/releases  -> [백업 서버] /backups/server1/releases
[server2] /my/backups/jenkins -> [백업 서버] /backups/server2/jenkins
```

여러 방법들이 있지만, 백업 서버에 SSH가 열려 있다면 SSHFS를 이용해서 간단히 마운트 할 수 있다. SSHFS(Secure SHell FileSystem)는 SFTP로 파일을 전송하는 클라이언트이다.

위의 `server1`, `server2` 등 마운트할 클라이언트 쪽에서만 아래 명령을 실행하면 된다.


# SSHFS 설치

SSHFS 설치는 패키지 매니저를 쓰면 간단하다. 시스템에 맞는 명령을 사용하자.

```bash
$ yum install sshfs
$ dnf install sshfs
$ sudo apt-get install sshfs
```


# 마운팅할 경로 생성

```bash
$ mkdir -p /my/backups

$ # 보통 마운트 경로는 /mnt 경로 아래로 생성한다.
$ sudo mkdir -p /mnt/backups
```


# SSHFS를 이용한 연결(mount)

```bash
$ # user와 x.x.x.x는 백업 서버의 사용자와 IP를 적어준다.
$ sshfs user@x.x.x.x:/backups/server1 /my/backups

$ # Ubuntu, Debian의 경우
$ sudo sshfs -o allow_other user@x.x.x.x:/backups/server1 /my/backups
```

위와 같이 실행하면 명령을 실행한 `server1`의 `/my/backups` 경로는 `백업서버`(x.x.x.x)의 `/backups/server1`에 연결된다.

```bash
$ # 연결이 끊기면 재접속할 수 있게 설정
$ sshfs -o reconnect user@x.x.x.x:/backups/server1 /my/backups

$ # SSH key 인증 방식을 쓴다면 인증 파일을 지정해 줄 수도 있다.
$ sshfs -o IdentityFile=~/.ssh/id_rsa user@x.x.x.x:/backups/server1 /my/backups
```


# 연결(mount) 확인

`df` 명령으로 마운팅된 경로와 해당 서버의 경로를 알 수 있다.

```bash
$ df -hT
Filesystem                             Type        Size  Used Avail Use% Mounted on
/dev/sda4                              xfs         268G  122G  147G  46% /
devtmpfs                               devtmpfs    7.8G     0  7.8G   0% /dev
tmpfs                                  tmpfs       7.8G     0  7.8G   0% /dev/shm
tmpfs                                  tmpfs       7.8G   57M  7.7G   1% /run
tmpfs                                  tmpfs       7.8G     0  7.8G   0% /sys/fs/cgroup
/dev/sda2                              xfs        1014M  130M  885M  13% /boot
tmpfs                                  tmpfs       1.6G     0  1.6G   0% /run/user/1000
user@my-backup-server:/backups/server1 fuse.sshfs   50G  4.2G   46G   9% /my/backups
```

서버가 재시작 되어도 항상 연결되어 있게 하려면 `/etc/fstab` 파일을 수정해 주면 된다.

```
sshfs#user@x.x.x.x:/backups/server1 /my/backups fuse.sshfs defaults 0 0

# 옵션이 필요하면 적어준다
sshfs#user@x.x.x.x:/backups/server1 /my/backups fuse.sshfs IdentityFile=~/.ssh/id_rsa defaults 0 0
sshfs#user@x.x.x.x:/backups/server1 /my/backups fuse.sshfs reconnect defaults 0 0
```


# 연결 해제(unmount)

연결을 끊고 싶으면 `umount` 명령을 사용한다.

```bash
$ umount /my/backups
```


# 참고

- [How to Mount Remote Linux Filesystem or Directory Using SSHFS Over SSH](https://www.tecmint.com/sshfs-mount-remote-linux-filesystem-directory-using-ssh/)
