---
layout: post
title:  "[React] Browserify SyntaxError: Unexpected token"
date:   2016-01-01 00:07:36 +0900
published: true
categories: react
tags: react browserify babelify preset syntax-jsx babel-preset-react
---

[React 샘플](http://facebook.github.io/react/docs/getting-started.html) browserify로 컴파일 하려는데, html 태그에서 SyntaxError 나면서 안 됨 T_T

```bash
$ browserify -t babelify main.js -o bundle.js
SyntaxError: /Users/sigel/Desktop/tmp/react/main.js: Unexpected token (5:2)
 3 |
 4 | ReactDOM.render(
> 5 |   <h1>Hello, world!</h1>,
   |   ^
 6 |   document.getElementById('example')
 7 | );
 8 |
 ```

babelify로 transform할 때 JSX 구문을 인식할 수 있게 preset을 추가해 줘야 한다.

```bash
$ npm install babel-plugin-syntax-jsx
http://babeljs.io/docs/plugins/syntax-jsx/
```

음.. React를 사용하기 위해 여러 preset을 추가해줘야 하니 (귀찮으니) react preset을 추가해 줘도 된다.

```bash
$ npm install babel-preset-react
http://babeljs.io/docs/plugins/preset-react/
```

그리고 babelify가 preset을 가지고 transform할 수 있도록 .babelrc 파일에 설정을 추가하거나, browserify 명령에 옵션으로 추가하자.

```javascript
{
 "presets": ["react"]
}
```

또는.. 아래처럼 옵션으로.. browserify 명령에 옵션을 추가할 때는 띄워쓰기와 각괄호에 주의!! (띄워쓰기 때문에 삽질한거 생각하면 T_T)

```bash
$ browserify -t [ babelify --presets [ react ] ] main.js -o bundle.js
```
