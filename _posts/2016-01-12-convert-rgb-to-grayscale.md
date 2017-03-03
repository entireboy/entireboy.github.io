---
layout: post
title:  "Convert RGB to grayscale"
date:   2016-01-12 15:20:36 +0900
categories: [ colour ]
tags: [ color, colour, convert, rgb, grayscale ]
---

요즘 이미지에서 색깔을 뽑아오는 일들이 좀 있다. 색깔을 뽑아서 평균 값을 구한다거나 회색톤(grayscale)으로 바꾸고 다시 흑백(monochrome)으로 바꾼다거나..

오늘은 이미지를 회색톤으로 바꿀 일이 생겨서 찾아보던 중 RGB를 grayscale로 바꾸는 공식을 알게 됐다. RGB를 모두 합쳐서 3으로 나누는 값이 아니었다.

```
Y = 0.299 * R + 0.587 * G + 0.114 * B
Y = 0.2126 * R + 0.7152 * G + 0.0722 * B
```

[위키피디아](https://en.wikipedia.org/wiki/Grayscale)에 소개된 계산법은 2가지가 있는데, 비디오 표준 방식 등에 따라 차이가 있는 것 같다. PAL, NTSC 등에서 사용되는 [Y'UV](https://en.wikipedia.org/wiki/YUV), [Y'IQ](https://en.wikipedia.org/wiki/YIQ) 색공간에서는 첫번째 수식으로 사용하고, HDTV 등에서 사용되는 [IUT-R BT.709](https://en.wikipedia.org/wiki/Rec._709)는 두번째 수식을 사용하는 것 같다. (해석이 구려서.. 자세한 내용은 [위키피디아](https://en.wikipedia.org/wiki/Grayscale#Luma_coding_in_video_systems) 참조)
