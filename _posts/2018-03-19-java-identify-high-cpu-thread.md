---
layout: post
title:  "[Java] CPU 많이 쓰는(high CPU) 스레드 찾기"
date:   2018-03-19 23:18:00 +0900
published: true
categories: [ java ]
tags: [ java, identify, high, cpu, thread, lwp, ps, dump ]
---

아무 일도 하지 않는 Java 프로세스가 CPU를 너무 먹고 있는게 발견됐다. 뭘까?? 누가 쓰고 있는걸까?? 그것도 애매한 13% 라니..

![High CPU]({{ site.baseurl }}/assets/img/2018-03-19-java-identify-high-cpu-thread.png)

우선 `ps` 명령으로 어떤 스레드가 CPU를 쓰고 있는지 확인을 했다. `-m`옵션으로 스레드 정보를 함께 출력한다. 그리고 `-o` 옵션으로 CPU 사용률(`pcpu`, %CPU)과 스레드 번호(`lwp`)를 함께 출력한다. `pcpu`, `lwp` 이외의 `-o` 옵션으로 가능한 내용은 [ps man page](http://linuxcommand.org/lc3_man_pages/ps1.html)의 `STANDARD FORMAT SPECIFIERS` 섹션에서 확인할 수 있다.

```bash
$ ps -mo pcpu,lwp -p 46783
%CPU   LWP
 100     -
 0.0 46783
 0.0 46785
99.9 47612
 0.0 46792
 0.0 46793
 0.0 46794

 ...
```

`99.9%`를 사용하는 스레드 `47612`를 발견했다. 왜 `99.9%`를 쓰고 있는데 `13%`라고 탐지가 된걸까?? 그건 이 서버의 *CPU가 8개* 이기 때문이다. 1개의 CPU만을 `100%` 가까이 쓰고 있고, 나머지 CPU도 조금씩 쓰고 있기 때문에 전체의 1/8 수준인 `13%`로 측정된 것이다.


`47612` 이 값은 10진수 값이고 thread dump에 남는 스레드 번호는 16진수라 16진수로 변환을 하고 thread dump 파일에서 찾는다. `47612`는 16진수로 `B9FC`이다. ([Decimal to Hexadecimal Converter](https://www.binaryhexconverter.com/decimal-to-hex-converter) 같은 툴을 사용하면 편하게 진법변환을 할 수 있다.)

`B9FC`를 스레드 덤프에서 찾아보면 `nid=0xb9fc`와 같이 적혀 있는걸 찾을 수 있다.

```bash
"XXX_THREAD" #41 daemon prio=5 os_prio=0 tid=0x00007f1ff1f69800 nid=0xb9fc runnable [0x00007f1fc17f4000]
   java.lang.Thread.State: RUNNABLE
        at com.test.my.test.TestImpl$1.run(TestImpl.java:147)
        at java.lang.Thread.run(Thread.java:745)

   Locked ownable synchronizers:
        - None
```

자.. 범인을 찾았으니, 이제 이 스레드를 어떻게 할꼬.. 흠..


# 추가 툴

## VisualVM - Profiler

VisualVM [Startup Profiler plugin](https://visualvm.github.io/startupprofiler.html)을 통해서 모니터링을 할 수 있다.

예의 없는 오라클 친구들이 문서 링크를 모두 죽여 버려서, [예전 버전 문서](https://docs.oracle.com/javase/8/docs/technotes/guides/visualvm/profiler.html)만 남아 있다. (또는 VisualVM Document에 있는 [Profile Applications](https://htmlpreview.github.io/?https://raw.githubusercontent.com/visualvm/visualvm.java.net.backup/master/www/profiler.html) 참고)

Remote 서버라도 `jstatd`만 실행되고 있다면 VisualVM으로 접속이 가능하다. ([Working with Remote Applications](https://htmlpreview.github.io/?https://raw.githubusercontent.com/visualvm/visualvm.java.net.backup/master/www/applications_remote.html) 참고)


## IntelliJ - Profiling tools

IntelliJ 2021 버전 이상에서 [Profiling 도구](https://www.jetbrains.com/help/idea/cpu-profiler.html)들을 몇 가지 제공한다. 단, Ultimate 버전에서..

- [Async Profiler](https://www.jetbrains.com/help/idea/async-profiler.html)
- [Java Flight Recorder](https://www.jetbrains.com/help/idea/java-flight-recorder.html)
- [Run with a profiler](https://www.jetbrains.com/help/idea/run-with-profiler.html)
- [Read the profiling report](https://www.jetbrains.com/help/idea/read-the-profiling-report.html)
- [Analyze memory snapshots](https://www.jetbrains.com/help/idea/analyze-hprof-memory-snapshots.html)
- [CPU and memory live charts](https://www.jetbrains.com/help/idea/cpu-and-memory-live-charts.html)

(Remote 서버에도 접근이 가능한지는 아직 모르겠음)


# 참고

- [ps man page](http://linuxcommand.org/lc3_man_pages/ps1.html)
- [Decimal to Hexadecimal Converter](https://www.binaryhexconverter.com/decimal-to-hex-converter)
- [VisualVM - Startup Profiler plugin](https://visualvm.github.io/startupprofiler.html)
- [IntelliJ Profiling tools](https://www.jetbrains.com/help/idea/cpu-profiler.html)
