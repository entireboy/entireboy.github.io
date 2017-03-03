---
layout: post
title:  "[React] Uncaught Invariant Violation: _registerComponent(...): Target container is not a DOM element."
date:   2016-01-01 00:19:36 +0900
published: true
categories: [ react ]
tags: [ react, browserify, babelify, preset, syntax-jsx, babel-preset-react ]
---

```html
<!DOCTYPE html>
<html>
 <head>
   <meta charset="UTF-8" />
   <title>Hello React!</title>
   <script type="text/javascript" src="bundle.js"></script>
 </head>
 <body>
   <div id="example"></div>
 </body>
</html>
// main.js
var React = require('react');
var ReactDOM = require('react-dom');ReactDOM.render(
 <h1>Hello, world!</h1>,
 document.getElementById('example')
);
```

아래처럼 target DOM이 존재하지 않는다고 console에 에러가 찍힌다. 'example'은 분명 페이지에 내에 존재하는 DOM인데 못 찾는다. 슬프다.

```
Uncaught Invariant Violation: _registerComponent(...): Target container is not a DOM element.
```

잘 생각해 보면 순서가 바꼈다. (멍충이) `<script>`는 `<head>`안에 있어서 실행되는 시점이 `<body>`가 그러지기 이전일 수 있다. 대부분이 그려지기 이전일 것이다. 그러니 당연히 DOM이 없다고 나오지..

아래처럼 바꾸자. `<script>`는 코드 맨아래로 넣자.

```html
<!DOCTYPE html>
<html>
 <head>
   <meta charset="UTF-8" />
   <title>Hello React!</title>
 </head>
 <body>
   <div id="example"></div>

   <script type="text/javascript" src="뭐_이거저거.js"></script>
   <script type="text/javascript" src="bundle.js"></script>
 </body>
</html>
```
