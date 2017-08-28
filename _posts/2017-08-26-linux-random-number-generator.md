---
layout: post
title:  "[Linux] Random number generator (RNG) - available entropy"
date:   2017-08-26 21:18:00 +0900
published: true
categories: [ linux ]
tags: [ linux, unix, nix, system, random, random number, rng, random number generator, random genrator, entropy, entropy pool, pool, rng-tools, /dev/random, /dev/urandom ]
---

# RNG (Random number generator)

코드리뷰를 위해 Upsource([https://www.jetbrains.com/upsource/](https://www.jetbrains.com/upsource/))를 설치하려는데 이런 에러와 함께 설치가 안 된다. 난수(random)를 뽑기 위해 커널에서 사용되는 부분에 문제가 있는 것이다. 충분하고 고른 난수 발생이 되지 않는 경우에 발생한다.

```bash
$ ./upsource.sh start
Starting Upsource...
* Configuring JetBrains Upsource 2017.2
* JetBrains Upsource 2017.2 runtime environment is successfully configured
[Upsource Error] Failed to start JetBrains Upsource 2017.2 due to unexpected exception: Native random generator does not seem to have enough entropy for JetBrains Upsource 2017.2 to start.
[Upsource Error] You can fix it by switching to PRNG (with -Djava.security.egd=/dev/zrandom) or by reconfiguring your operation system to provide more random bits.
Upsource process finished
Launcher is exiting
Upsource failed to start
```

해결 방법은 [이 포스트의 맨 아래에 있는 방법](#install-rng-tools)으로 `rng-tools`를 설치하고 설정해 주면 된다. `rng-tools`가 아닌 다른 서드파티 툴을 사용해도 되고 `Java` 명령 실행 시에 `-Djava.security.egd=/dev/zrandom`을 적어주어도 되지만, Upsource 설치 메뉴얼(사실 [Hub support 페이지][hub support]. Upsource는 Hub를 사용한다.)에는 `rng-tools` 설치를 더 권하고 있다. 단점이라면 이 방법은 *nix 시스템에서만 가능한 방법이다.

> 1\. Use a programmatic PRNG instead of default RNG supplied by the OS. To do so, use the Java start parameter `-Djava.security.egd=/dev/zrandom`
>
> OR
>
> 2\. Install rng-tools package and configure it to supply /dev/urandom. Check this article describing the solution. This workaround is **preferable to the first option**, but, unfortunately, it is applicable only to the *nix systems.


# Random number

Linux/Unix 시스템에서 난수를 뽑을 때 `/dev/random`을 사용하게 된다.

보안을 위해 다음에 뽑을 랜덤 숫자를 알아내기 어렵게 해야 한다. 이를 측정하기 위해 엔트로피(entropy) 라는 개념을 사용한다. 랜덤으로 뽑은 숫자를 얼마나 예측하기 어렵고 고른 숫자가 나오는가 정도를 나타낸다. 랜덤 숫자를 뽑을 때 마다 이 엔트로피를 사용하기 때문에, 많이 뽑으면 엔트로피가 떨어진다.


# Entropy pool

때문에, 미리 엔트로피 풀(entropy pool)에 난수 생성을 위한 데이터를 넣어둔다. 엔트로피 풀은 2가지 방법으로 사용된다. 풀 안의 내용으로 랜덤 숫자를 뽑아내거나, 커널에 의해 다시 엔트로피 풀을 채우기 위해 사용된다.

이 수치는 아래 명령어로 그 수치를 확인할 수 있고, 이 값이 100-200 이하의 숫자라면 좋지 않은 상태이다. 마우스나 키보드가 없는 가상머신에서 이런 경우가 많이 생긴다.

```bash
$ cat /proc/sys/kernel/random/entropy_avail
```


# Entropy draining

엔트로피 풀은 아래 그림과 같은 형태로 채워지고 사용된다. 마우스나 키보드, 인터럽트 등에서 랜덤한 값들이 많이 발생하기 때문에 풀을 채우기 좋은 소스가 된다. 때문에 마우스나 키보드가 없는 가상머신들에서 풀이 부족한 경우가 많이 발생한다.

{% include image.html file='/assets/img/2017-08-26-linux-random-number-generator.png' alt='Entropy pool' %}

난수를 만들기 위해 `/dev/random` 파일과 `/dev/urandom` 파일을 사용하게 되는데, `/dev/random` 과 같은 경우는 블로킹 되기 때문에 엔트로피가 충분히 올라가기 전까지는 응답을 대기하고 있는 상태가 된다.


# <a name="install-rng-tools"></a> Fill the entropy pool with rng-tools

가상머신처럼 자연적으로 풀을 채울 수 없다면 `rng-tools` [GNU Hurd rng-tools][rng-tools] 같은 툴을 설치해서 사용해서 채울 수 있다.

```bash
$ apt-get install rng-tools
```

그리고 `/etc/default/rng-tools` 파일을 열어서, 아래와 같이 `HRNGDEVICE=/dev/urandom` 라인을 추가한다.

```bash
# Configuration for the rng-tools initscript
# $Id: rng-tools.default,v 1.1.2.5 2008-06-10 19:51:37 hmh Exp $

# This is a POSIX shell fragment

# Set to the input source for random data, leave undefined
# for the initscript to attempt auto-detection.  Set to /dev/null
# for the viapadlock and tpm drivers.
#HRNGDEVICE=/dev/hwrng
#HRNGDEVICE=/dev/null
HRNGDEVICE=/dev/urandom

# Additional options to send to rngd. See the rngd(8) manpage for
# more information.  Do not specify -r/--rng-device here, use
# HRNGDEVICE for that instead.
#RNGDOPTIONS="--hrng=intelfwh --fill-watermark=90% --feed-interval=1"
#RNGDOPTIONS="--hrng=viakernel --fill-watermark=90% --feed-interval=1"
#RNGDOPTIONS="--hrng=viapadlock --fill-watermark=90% --feed-interval=1"
#RNGDOPTIONS="--hrng=tpm --fill-watermark=90% --feed-interval=1"
```

`rng-tools` 데몬을 시작하면 끝이다.

```bash
$ /etc/init.d/rng-tools start
```

설치 전 사용 가능한 엔트로피와 설치 후를 비교해 보면, 풀이 가득가득 찬 것을 볼 수 있다.

```bash
$ cat /proc/sys/kernel/random/entropy_avail
169
$ ###########################
$ #   .. rng-tools 설치 ..   #
$ ###########################
$ cat /proc/sys/kernel/random/entropy_avail
3083
```


# 참고

- [Hub hangs up on start][hub support]
- [Helping The Random Number Generator To Gain Enough Entropy With rng-tools (Debian Lenny)](https://www.howtoforge.com/helping-the-random-number-generator-to-gain-enough-entropy-with-rng-tools-debian-lenny)
- [Ensuring Randomness with Linux's Random Number Generator](https://blog.cloudflare.com/ensuring-randomness-with-linuxs-random-number-generator/)
- [Check available entropy in Linux](https://major.io/2007/07/01/check-available-entropy-in-linux/)
- [/dev/random - Wikipedia](https://en.wikipedia.org/wiki//dev/random)
- [GNU Hurd rng-tools][rng-tools]

[rng-tools]: https://www.gnu.org/software/hurd/user/tlecarrour/rng-tools.html
[hub support]: https://hub-support.jetbrains.com/hc/en-us/articles/206545269-Hub-hangs-up-on-start
