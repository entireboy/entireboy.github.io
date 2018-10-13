---
layout: post
title:  "[Ansible] jinja2 template comment({#) escape"
date:   2018-01-29 22:18:00 +0900
published: true
categories: [ ansible ]
tags: [ ansible, jinja2, template, module, escape, comment ]
---

[Ansible](https://www.ansible.com/)을 사용하서 [template 모듈](http://docs.ansible.com/ansible/latest/template_module.html)을 사용하면 파일의 중간중간을 변수로 바꿔 넣을 수 있어서 편하다. 이 template 모듈은 [Jinja2](http://jinja.pocoo.org/) template을 사용한다.

Jinja2에서 주석(comment)은 `{# ... #}`로 표현하는데, shell script를 만들다 보면 문자열이나 배열의 길이를 구하기 위해 `{#`를 사용해야 하는 경우가 종종 생긴다. 이럴 때 `{#`을 jinja2 주석으로 판단해서 아래와 같은 에러(`Missing end of comment tag`)를 뱉는 경우가 종종 생긴다.

```bash
TASK [system/test : template test] **********************************************************
failed: [my-server] (item={u'dest': u'vhost.conf.10000', u'src': u'vhost.conf'})
=> {"failed": true, "item": {"dest": "vhost.conf.10000", "src": "vhost.conf"},
"msg": "AnsibleError: template error while templating string: Missing end of comment tag.
String: for (( i=0;i<${#APP_PORTS[@]};i++ )); do\n echo ${i}\ndone\n"}
```

이럴 때 `{#`를 `{% raw %}{{{% endraw %} '{#' }}`로 바꿔주면 된다. 참 요상한 내용이 되어버려서 template을 알아보기 힘들지만, jinja2 document에는 [escape](http://jinja.pocoo.org/docs/2.10/templates/#escaping)를 이렇게 하라고 가이드가 되어 있다. T_T

```bash
for (( i=0;i<${#APP_PORTS[@]};i++ )); do
  echo ${i}
done

--> 안 이쁘지만 아래처럼 바꾸면 된다.

for (( i=0;i<${% raw %}{{{% endraw %} '{#' }}APP_PORTS[@]};i++ )); do
  echo ${i}
done
```


# 참고

- [Comments - Jinja2 Template Designer Documentation](http://jinja.pocoo.org/docs/2.10/templates/#comments)
- [Escaping - Jinja2 Template Designer Documentation](http://jinja.pocoo.org/docs/2.10/templates/#escaping)
