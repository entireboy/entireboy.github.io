---
layout: post
title:  "[Jekill] jekyll 문법 피하기 - escape liquid template"
date:   2017-07-30 21:18:00 +0900
published: true
categories: [ jekyll ]
tags: [ jekyll, liquid, escape, syntax, brace ]
---

[Jekyll](https://jekyllrb.com/)로 글을 쓰다 보면 [Liquid](https://shopify.github.io/liquid/) 문법을 쓰고 싶은 경우가 있다.

`{% raw %}{% .. %}{% endraw %}` 라든지 `{% raw %}{{ .. }}{% endraw %}` 같은 형태의 구문이다. 그대로 쓰게 되면 샘플 코드를 실행하려고 해서 문제가 된다. 이렇 때는 [raw tag](https://shopify.github.io/liquid/tags/raw/)를 사용하면 된다.

```html
{% raw %}{% raw %} {% some exp %} {% endraw %}{% raw %}{%{% endraw %} endraw %}
```

요렇게 사용하자.

```html
This is escape syntax:
{% raw %}{% raw %}{% if foo %}{{ foo }}{% endif %}{% endraw %}{% raw %}{%{% endraw %} endraw %}
```

위처럼 쓰면 아래처럼 표시된다. raw tag에 싸인 `{% raw %}{% .. %}{% endraw %}`와 `{% raw %}{{ .. }}{% endraw %}`는 모두 예외처리될 것이다.

```html
This is escape syntax:
{% raw %}{% if foo %}{{ foo }}{% endif %}{% endraw %}
```

# 참고

- [How to escape liquid template tags? - Stack Overflow](https://stackoverflow.com/questions/3426182/how-to-escape-liquid-template-tags)
- [Raw - Liquid template language](https://shopify.github.io/liquid/tags/raw/)
