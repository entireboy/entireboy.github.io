---
layout: post
title:  "[Jekyll] 다른 파일 끼워넣기 - include"
date:   2017-07-29 23:18:00 +0900
published: true
categories: [ jekyll ]
tags: [ jekyll, liquid, include, file ]
---

# Include files

[Jekyll](https://jekyllrb.com/)을 사용할 때 `{% raw %}{% include %}{% endraw %}`를 사용하면 다른 파일을 쉽게 끼워넣을 수 있다. 끼워넣고 싶은 파일을 전달인자로 주면 된다.

```html
{% raw %}{% include header.html %} {% endraw %}
```

끼워넣을 파일(`header.html`)은 `_include` 폴더 안에 있어야 한다. 현재 파일의 상대 경로로 끼워넣고 싶으면 `{% raw %}{% include_relative %}{% endraw %}`를 사용하면 된다.

```html
{% raw %}{% include_relative somewhere/header.html %}{% endraw %}
```

# Passing parameters

끼워넣을 파일에 전달인자를 함께 넘겨줄 수 있다. 예를 들어, 페이지에 이미지 넣어주는 파일을 만들어 두고 이미지 url만 바꾸어서 include할 수 있다.

```html
{% raw %}<a href="{{ include.file }}" target="_blank" class="image">
  <img src="{{ include.file }}" alt="{% if include.alt %}{{ include.alt }}{% else %}Image{% endif %}">
</a>{% endraw %}
```

위처럼 끼워넣을 `image.html` 파일을 만들어 둔다. include할 때 마다 이미지 위치(`file`)와 설명(`alt`)를 받아서 사용하면 된다.

```html
{% raw %}{% include image.html file='hello.jpg' alt='Hello' %}
{% include image.html file='bye.jpg' alt='Bye' %}{% endraw %}
```

전달인자는 `{% raw %}{{ include.file }}{% endraw %}`, `{% raw %}{{ include.alt }}{% endraw %}` 처럼 `include.`을 사용하면 된다. include에 넘겨준 전달인자가 map처럼 들어 있다.


# 참고

- [Includes - Jekyll docs](https://jekyllrb.com/docs/includes/)
- [Liquid](https://shopify.github.io/liquid/)
