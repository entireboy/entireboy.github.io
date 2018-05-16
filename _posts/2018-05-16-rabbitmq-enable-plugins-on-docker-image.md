---
layout: post
title:  "[RabbitMQ] RabbitMQ 플러그인 켜진 Docker 이미지 만들기"
date:   2018-05-16 23:18:00 +0900
published: true
categories: [ rabbitmq, docker ]
tags: [ rabbitmq, docker, image, enable, plugin ]
---

RabbitMQ의 [management plugin](https://www.rabbitmq.com/management.html)을 켜기 위해 다음 명령을 실행하려 한다.
```bash
$ rabbitmq-plugins enable rabbitmq_management
```

단순히 dockerfile에 이렇게 넣어주었는데..

```docker
RUN rabbitmq-plugins enable rabbitmq_management
```

docker 이미지를 만들고 실행하면, 엄청난 알 수 없는 오류가 발생한다. T_T

```bash
$ docker run -it --rm my/test
[root@764e85b5ba01 /]# rabbitmq-server on
# 2018-05-16 16:34:24 Error when reading /var/lib/rabbitmq/.erlang.cookie: eacces
2018-05-16 16:34:24 crash_report
    initial_call: {auth,init,['Argument__1']}
    pid: <0.48.0>
    registered_name: []
    error_info: {error,"Error when reading /var/lib/rabbitmq/.erlang.cookie: eacces",[{auth,init_cookie,0,[{file,"auth.erl"},{line,286}]},{auth,init,1,[{file,"auth.erl"},{line,140}]},{gen_server,init_it,2,[{file,"gen_server.erl"},{line,365}]},{gen_server,init_it,6,[{file,"gen_server.erl"},{line,333}]},{proc_lib,init_p_do_apply,3,[{file,"proc_lib.erl"},{line,247}]}]}
    ancestors: [net_sup,kernel_sup,<0.36.0>]
    message_queue_len: 0
    messages: []
    links: [<0.46.0>]
    dictionary: []
    trap_exit: true
    status: running
    heap_size: 610
    stack_size: 27
    reductions: 954
2018-05-16 16:34:24 supervisor_report
    supervisor: {local,net_sup}
    errorContext: start_error
    reason: {"Error when reading /var/lib/rabbitmq/.erlang.cookie: eacces",[{auth,init_cookie,0,[{file,"auth.erl"},{line,286}]},{auth,init,1,[{file,"auth.erl"},{line,140}]},{gen_server,init_it,2,[{file,"gen_server.erl"},{line,365}]},{gen_server,init_it,6,[{file,"gen_server.erl"},{line,333}]},{proc_lib,init_p_do_apply,3,[{file,"proc_lib.erl"},{line,247}]}]}
    offender: [{pid,undefined},{id,auth},{mfargs,{auth,start_link,[]}},{restart_type,permanent},{shutdown,2000},{child_type,worker}]
2018-05-16 16:34:25 supervisor_report
    supervisor: {local,kernel_sup}
    errorContext: start_error
    reason: {shutdown,{failed_to_start_child,auth,{"Error when reading /var/lib/rabbitmq/.erlang.cookie: eacces",[{auth,init_cookie,0,[{file,"auth.erl"},{line,286}]},{auth,init,1,[{file,"auth.erl"},{line,140}]},{gen_server,init_it,2,[{file,"gen_server.erl"},{line,365}]},{gen_server,init_it,6,[{file,"gen_server.erl"},{line,333}]},{proc_lib,init_p_do_apply,3,[{file,"proc_lib.erl"},{line,247}]}]}}}
    offender: [{pid,undefined},{id,net_sup},{mfargs,{erl_distribution,start_link,[]}},{restart_type,permanent},{shutdown,infinity},{child_type,supervisor}]
2018-05-16 16:34:25 crash_report
    initial_call: {application_master,init,['Argument__1','Argument__2','Argument__3','Argument__4']}
    pid: <0.35.0>
    registered_name: []
    error_info: {exit,{{shutdown,{failed_to_start_child,net_sup,{shutdown,{failed_to_start_child,auth,{"Error when reading /var/lib/rabbitmq/.erlang.cookie: eacces",[{auth,init_cookie,0,[{file,"auth.erl"},{line,286}]},{auth,init,1,[{file,"auth.erl"},{line,140}]},{gen_server,init_it,2,[{file,"gen_server.erl"},{line,365}]},{gen_server,init_it,6,[{file,"gen_server.erl"},{line,333}]},{proc_lib,init_p_do_apply,3,[{file,"proc_lib.erl"},{line,247}]}]}}}}},{kernel,start,[normal,[]]}},[{application_master,init,4,[{file,"application_master.erl"},{line,134}]},{proc_lib,init_p_do_apply,3,[{file,"proc_lib.erl"},{line,247}]}]}
    ancestors: [<0.34.0>]
    message_queue_len: 1
    messages: [{'EXIT',<0.36.0>,normal}]
    links: [<0.34.0>,<0.33.0>]
    dictionary: []
    trap_exit: true
    status: running
    heap_size: 987
    stack_size: 27
    reductions: 250
2018-05-16 16:34:25 std_info
    application: kernel
    exited: {{shutdown,{failed_to_start_child,net_sup,{shutdown,{failed_to_start_child,auth,{"Error when reading /var/lib/rabbitmq/.erlang.cookie: eacces",[{auth,init_cookie,0,[{file,"auth.erl"},{line,286}]},{auth,init,1,[{file,"auth.erl"},{line,140}]},{gen_server,init_it,2,[{file,"gen_server.erl"},{line,365}]},{gen_server,init_it,6,[{file,"gen_server.erl"},{line,333}]},{proc_lib,init_p_do_apply,3,[{file,"proc_lib.erl"},{line,247}]}]}}}}},{kernel,start,[normal,[]]}}
    type: permanent
{"Kernel pid terminated",application_controller,"{application_start_failure,kernel,{{shutdown,{failed_to_start_child,net_sup,{shutdown,{failed_to_start_child,auth,{\"Error when reading /var/lib/rabbitmq/.erlang.cookie: eacces\",[{auth,init_cookie,0,[{file,\"auth.erl\"},{line,286}]},{auth,init,1,[{file,\"auth.erl\"},{line,140}]},{gen_server,init_it,2,[{file,\"gen_server.erl\"},{line,365}]},{gen_server,init_it,6,[{file,\"gen_server.erl\"},{line,333}]},{proc_lib,init_p_do_apply,3,[{file,\"proc_lib.erl\"},{line,247}]}]}}}}},{kernel,start,[normal,[]]}}}"}
Kernel pid terminated (application_controller) ({application_start_failure,kernel,{{shutdown,{failed_to_start_child,net_sup,{shutdown,{failed_to_start_child,auth,{"Error when reading /var/lib/rabbitmq

Crash dump is being written to: /var/log/rabbitmq/erl_crash.dump...done
```

이리저리 찾아보다 보니 `rabbitmq-plugins`에 `--offline`이라는 옵션이 있다. 실제 설정을 적용하는게 아니라 플러그인 설정 파일만 변경해 두는 것 같다. (c.f. [rabbitmq-plugins man page](https://www.rabbitmq.com/man/rabbitmq-plugins.8.html)) 요로코롬 `--offline` 옵션을 주고 실행하면 위에 봤던 에러 없이 슝슝 잘 돈다. Docker 이미지처럼 바로 적용해서 쓰는게 아니라면 `--offline` 옵션을 주자.

```docker
FROM centos:centos7

RUN yum install -y curl wget sudo telnet glibc-common net-tools

RUN localedef -i ko_KR -f UTF-8 ko_KR.UTF-8
ENV LANG="ko_KR.UTF-8"

RUN yum install -y https://github.com/rabbitmq/erlang-rpm/releases/download/v20.3.4/erlang-20.3.4-1.el7.centos.x86_64.rpm
RUN yum install -y https://github.com/rabbitmq/rabbitmq-server/releases/download/v3.7.5/rabbitmq-server-3.7.5-1.el7.noarch.rpm

RUN rabbitmq-plugins enable --offline rabbitmq_management

EXPOSE 4369 5671 5672 25672
# CMD ["rabbitmq-server"]
```


# 참고

- [management plugin](https://www.rabbitmq.com/management.html)
- [https://www.rabbitmq.com/man/rabbitmq-plugins.8.html](rabbitmq-plugins man page)
