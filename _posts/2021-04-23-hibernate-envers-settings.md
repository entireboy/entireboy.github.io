---
layout: post
title:  "[Hibernate Envers] Hibernate Envers 사용할 때 필요한 이런 저런 설정들"
date:   2021-04-23 22:18:00 +0900
published: true
categories: [ hibernate ]
tags: [ hibernate, envers, audit, history, setting, config, db, database ]
---

> 이 설정들은 꾸준히 업데이트 될 수 있음

```yaml
org:
  hibernate:
    envers:
      audit_table_suffix: _audit
      store_data_at_delete: true # Delete 될 때, 현재 상태를 함께 저장한다. false 인 경우 null 로 저장됨. default: false
      do_not_audit_optimistic_locking_field: false # false 로 설정 해야 @Version 컬럼도 audit 테이블에 저장된다. default : true
```

[Envers 설정 목록](https://docs.jboss.org/hibernate/orm/5.4/userguide/html_single/Hibernate_User_Guide.html#envers-configuration)


# REV 컬럼 Long 타입으로 변경

`REVINFO` 테이블의 `REV` 컬럼은 int 에서 long 으로 변환해 줘야 한다. 추가/수정/삭제를 할 때 마다 `REV` 가 하나씩 증가하는데, 20억이 넘어가면 int 사이즈는 오버 플로우 되어버릴 것이다.

설정은 [[Hibernate Envers] REV(revision number)를 long으로 바꾸기]({{ site.baseurl }}{% post_url 2019-06-04-hibernate-change-envers-REV-revision-number-to-long %}) 참고


# `@JoinColumn`을 사용한 `@OneToMany` 인 경우

`@JoinColumn`을 사용한 `@OneToMany`로 양방향 연관관계인 경우, one 쪽에 `@JoinTable`이나 `@AuditMappedBy` 설정을 해줘야 한다. [21.22. @OneToMany with @JoinColumn](https://docs.jboss.org/hibernate/orm/5.4/userguide/html_single/Hibernate_User_Guide.html#_code_onetomany_code_with_code_joincolumn_code)

```kotlin
@Entity
@Audited
@AuditTable("order_audit")
class Order(

  @OneToMany
  @AuditMappedBy(mappedBy = "order")
  private orderItems: List<OrderItems>,
)
```


# @Version을 사용하는 경우

`@Version` 을 사용해서 optimistic locking 을 사용하는 경우, audit 테이블에 함께 저장하기 위해서는 `do_not_audit_optimistic_locking_field` 설정을 해줘야 한다.

```yaml
org:
  hibernate:
    envers:
      do_not_audit_optimistic_locking_field: false # false 로 설정 해야 @Version 컬럼도 audit 테이블에 저장된다. default : true
```


# 삭제할 때도 스냅샷을 남기고 싶은 경우

팀/회사에서는 soft delete를 사용하고 있는데, Hibernate Envers는 hard delete 기준으로 만들어진 것 같다.

Envers는 데이터를 삭제하면 실제로 데이터를 지우는 형태로 `REVTYPE=2(del)` 와 모든 필드는 `null` 로 audit 테이블에 저장한다. 하지만, soft delete를 사용하면 일부 필드만 변경(`deleted=true`)을 하는 경우가 많고, 저장할 때의 값이 필요해질 때가 있다. 이런 경우, `store_data_at_delete` 설정을 해줘야 한다.

```yaml
org:
  hibernate:
    envers:
      store_data_at_delete: true # Delete 될 때, 현재 상태를 함께 저장한다. false 인 경우 null 로 저장됨. default: false
```

아래 이미지의 `111346`과 `111348`은 모두 audit 테이블의 삭제된 데이터인데, `11134O`은 `store_data_at_delete=true`설정으로 삭제된 데이터를 함께 저장한 경우이다.

{% include image.html file='/assets/img/2021/2021-04-23-hibernate-envers-settings.png' alt='Deleted data' %}


# 참고

- [[Hibernate Envers] REV(revision number)를 long으로 바꾸기]({{ site.baseurl }}{% post_url 2019-06-04-hibernate-change-envers-REV-revision-number-to-long %})
- [21.2. Configuration Properties](https://docs.jboss.org/hibernate/orm/5.4/userguide/html_single/Hibernate_User_Guide.html#envers-configuration)
- [21.22. @OneToMany with @JoinColumn](https://docs.jboss.org/hibernate/orm/5.4/userguide/html_single/Hibernate_User_Guide.html#_code_onetomany_code_with_code_joincolumn_code)
