---
layout: post
title:  "[Unicode] 눈에 안 보이는 BOM(U+FEFF, U+FFFE) 캐릭터"
date:   2018-07-30 22:18:00 +0900
published: true
categories: [ unicode ]
tags: [ unicode, bom, byte order, endianness, little endian, big endian, char, character ]
---

에러 메시지에 이상한 문자열 `<feff>`가 붙어 있고, 서버에 접속을 못 하는 에러가 발생했다.

```
Caused by: java.net.UnknownHostException: <feff>my-mongo.leocat.kr: Name or service not known
        at java.net.Inet4AddressImpl.lookupAllHostAddr(Native Method)
        at java.net.InetAddress$2.lookupAllHostAddr(InetAddress.java:928)
        at java.net.InetAddress.getAddressesFromNameService(InetAddress.java:1323)
        at java.net.InetAddress.getAllByName0(InetAddress.java:1276)
        at java.net.InetAddress.getAllByName(InetAddress.java:1192)
        at java.net.InetAddress.getAllByName(InetAddress.java:1126)
        at java.net.InetAddress.getByName(InetAddress.java:1076)
        at com.mongodb.ServerAddress.getSocketAddress(ServerAddress.java:186)
        ... 5 common frames omitted
```

다른 테스트 파일에서 host를 적어주고 테스트하면 잘 접속한다. 그런데 유독 아래 설정파일을 로딩할 때만 안 되는데, 아무리 눈씻고 봐도 잘못된 부분을 찾을 수 없었다. 그리고 `<feff>`라는 문자는 어디에도 없는 일반적인 yaml 파일이다.

{% include image.html file='/assets/img/2018-07-30-unicode-invisible-bom1.png' alt='invisible bom character' %}

뭔지 모르겠지만 오류 메시지의 `<feff>`로 검색을 시작했고, [BOM(Byte Order Mark)](https://en.wikipedia.org/wiki/Byte_order_mark) 일 수 있다는 [글을 발견](https://stackoverflow.com/questions/23211589/error1-1illegalcharacter-ufeff-when-compiling-on-android-studio)했다. UTF 파일은 처음 시작할 때 `U+FEFF`나 `U+FFFE` 같은 BOM 캐릭터를 적어주어서 바이트를 어느 방향으로 읽을지를 표시할 수 있다. ([little-endian](https://en.wikipedia.org/wiki/Endianness#Little), [big-endian](https://en.wikipedia.org/wiki/Endianness#Big))

아무래도 host명을 복붙하다가 BOM이 붙어 있는 형태로 복사된 것 같다.

실제로 BOM 인지 확인하기 위해 IntelliJ에서 파일 charset을 변경해보니, `?`로 숨어 있는 친구 발견 T_T `EUC-KR`이나 `ISO-8859-1` 같은 UTF 이외의 인코딩으로 변경하면 확인할 수 있을 것 이다.

{% include image.html file='/assets/img/2018-07-30-unicode-invisible-bom2.png' alt='bom character via non-unicode' %}

아무래도 host명을 복붙하다가 BOM이 붙어있는 형태로 복사된 것 같다. 그리고, 파일 처음에만 BOM을 넣을 수 있는 것이 아니라 중간에도 넣을 수 있다고 한다.

> If the BOM character appears in the middle of a data stream, Unicode says it should be interpreted as a "zero-width non-breaking space" (inhibits line-breaking between word-glyphs).
>
> \- from https://en.wikipedia.org/wiki/Byte_order_mark#Usage



# 참고

- [https://en.wikipedia.org/wiki/Byte_order_mark](https://en.wikipedia.org/wiki/Byte_order_mark)
- [Error(1,1)illegalcharacter '\ufeff' when compiling on android studio](https://stackoverflow.com/questions/23211589/error1-1illegalcharacter-ufeff-when-compiling-on-android-studio)
- [Endiannes Little-endian](https://en.wikipedia.org/wiki/Endianness#Little)
- [Endiannes Big-endian](https://en.wikipedia.org/wiki/Endianness#Big)
