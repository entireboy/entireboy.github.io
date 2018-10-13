---
layout: post
title:  "[Jenkins] 귀찮게 로그인 2번씩 해야 한다면 - LDAP timeout"
date:   2018-08-01 23:18:00 +0900
published: true
categories: [ jenkins ]
tags: [ jenkins, ldap, timeout, read, connection, access control, session ]
---

[Jenkins](https://jenkins.io/)에서 LDAP을 이용해 로그인 인증을 하는 경우, 오랜 시간 젠킨스를 사용하지 않거나 하여 LDAP 인증을 하지 않으면 LDAP과 연결이 끊기는 현상이 있다. 연결이 끊겼을 때 로그인을 시도하면, 비밀번호를 정확하게 입력해도 아래와 같은 오류 메시지와 함께 로그인이 실패하게 된다. 그래서 항상 2번씩 로그인을 하게 된다. (젠킨스 로그는 http://{JENKINS_HOST}/log/all 에서 확인할 수 있다.)

```
7월 31, 2018 10:12:14 오전 경고 hudson.security.LDAPSecurityRealm$LDAPAuthenticationManager authenticate
Failed communication with ldap server.
javax.naming.NamingException: LDAP response read timed out, timeout used:60000ms.; remaining name 'ou=members'
at com.sun.jndi.ldap.Connection.readReply(Connection.java:481)
at com.sun.jndi.ldap.LdapClient.getSearchReply(LdapClient.java:640)
at com.sun.jndi.ldap.LdapClient.search(LdapClient.java:563)
at com.sun.jndi.ldap.LdapCtx.doSearch(LdapCtx.java:1985)
at com.sun.jndi.ldap.LdapCtx.searchAux(LdapCtx.java:1844)
at com.sun.jndi.ldap.LdapCtx.c_search(LdapCtx.java:1769)
at com.sun.jndi.ldap.LdapCtx.c_search(LdapCtx.java:1786)
at com.sun.jndi.toolkit.ctx.ComponentDirContext.p_search(ComponentDirContext.java:418)
at com.sun.jndi.toolkit.ctx.PartialCompositeDirContext.search(PartialCompositeDirContext.java:396)
at com.sun.jndi.toolkit.ctx.PartialCompositeDirContext.search(PartialCompositeDirContext.java:378)
at javax.naming.directory.InitialDirContext.search(InitialDirContext.java:286)
at org.acegisecurity.ldap.LdapTemplate$3.doInDirContext(LdapTemplate.java:249)
at org.acegisecurity.ldap.LdapTemplate.execute(LdapTemplate.java:126)
Caused: org.acegisecurity.ldap.LdapDataAccessException: LdapCallback;LDAP response read timed out, timeout used:60000ms.; nested exception is javax.naming.NamingException: LDAP response read timed out, timeout used:60000ms.; remaining name 'ou=members'
at org.acegisecurity.ldap.LdapTemplate$LdapExceptionTranslator.translate(LdapTemplate.java:295)
at org.acegisecurity.ldap.LdapTemplate.execute(LdapTemplate.java:128)
at org.acegisecurity.ldap.LdapTemplate.searchForSingleEntry(LdapTemplate.java:246)
at org.acegisecurity.ldap.search.FilterBasedLdapUserSearch.searchForUser(FilterBasedLdapUserSearch.java:119)
at org.acegisecurity.providers.ldap.authenticator.BindAuthenticator.authenticate(BindAuthenticator.java:71)
at org.acegisecurity.providers.ldap.authenticator.BindAuthenticator2.authenticate(BindAuthenticator2.java:49)
at org.acegisecurity.providers.ldap.LdapAuthenticationProvider.retrieveUser(LdapAuthenticationProvider.java:233)
Caused: org.acegisecurity.AuthenticationServiceException: LdapCallback;LDAP response read timed out, timeout used:60000ms.; nested exception is javax.naming.NamingException: LDAP response read timed out, timeout used:60000ms.; remaining name 'ou=members'; nested exception is org.acegisecurity.ldap.LdapDataAccessException: LdapCallback;LDAP response read timed out, timeout used:60000ms.; nested exception is javax.naming.NamingException: LDAP response read timed out, timeout used:60000ms.; remaining name 'ou=members'
at org.acegisecurity.providers.ldap.LdapAuthenticationProvider.retrieveUser(LdapAuthenticationProvider.java:238)
at org.acegisecurity.providers.dao.AbstractUserDetailsAuthenticationProvider.authenticate(AbstractUserDetailsAuthenticationProvider.java:122)
at org.acegisecurity.providers.ProviderManager.doAuthentication(ProviderManager.java:200)

... 생략 ...
```

원인 중에 하나로 Jenkins에 설정된 default timeout은 60초인데, 네트웍이나 LDAP 설정에서 idle 상태일 때 연결을 끊는 시간이 60초 이하라 그럴 수 있다. 젠킨스 입장에서는 세션이 살아 있는줄 알았는데, 네트웍에서 이미 끊어진 상태이다. 이런 경우 젠킨스에서 LDAP timeout을 더 짧게 줘서 주기적으로 살아있음을 체크하도록 하여 연결을 유지하면 된다.

`Jenkins 관리` > `Configure Global Security` 메뉴(http://{JENKINS_HOST}/configureSecurity/)에서 LDAP 설정을 할 수 있다.

{% include image.html file='/assets/img/2018-08-01-jenkins-ldap-access-control-timeout1.png' alt='ldap config' %}

`Access Control` > `LDAP` > `ADVANCED SERVER CONFIGURATION...` 버튼을 누르면 여러 LDAP 접속 설정을 할 수 있다. `Environment Properties`에서 LDAP timeout설정(`com.sun.jndi.ldap.connect.pool.timeout`)을 추가해서 default timeout 60초 보다 작은 값으로 줄여주면 된다.

{% include image.html file='/assets/img/2018-08-01-jenkins-ldap-access-control-timeout2.png' alt='ldap timeout config' %}


# 참고

- [LDAP Plugin](https://wiki.jenkins.io/display/JENKINS/LDAP+Plugin)
- [LDAP response read timed out](https://confluence.atlassian.com/hipchatkb/ldap-response-read-timed-out-702715492.html)
- [Setting Timeout for Ldap Operations](https://docs.oracle.com/javase/tutorial/jndi/newstuff/readtimeout.html)
- [LDAP Naming Service Provider for the Java Naming and Directory Interface (JNDI)](https://docs.oracle.com/javase/8/docs/technotes/guides/jndi/jndi-ldap.html)
