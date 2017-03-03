---
layout: post
title:  "[Java] grayscale로 이미지 저장하기"
date:   2016-01-12 15:36:36 +0900
categories: [ java, colour ]
tags: [ java, color, colour, image, save, write, rgb, grayscale ]
---

RGB를 회색톤(grayscale)으로 변경해서 저장하기..

```java
BufferedImage image = ImageIO.read(new File("input.png"));

for(int y = 0; y < image.getHeight(); y++) {
   for(int x = 0; x < image.getWidth(); x++) {
       Color colour = new Color(image.getRGB(x, y));
//       Choose one from below
//       int Y = (int) (0.299 * colour.getRed() + 0.587 * colour.getGreen() + 0.114 * colour.getBlue());
       int Y = (int) (0.2126 * colour.getRed() + 0.7152 * colour.getGreen() + 0.0722 * colour.getBlue());
       image.setRGB(x, y, new Color(Y, Y, Y).getRGB());
   }
}

ImageIO.write(image, "png", new File("output.png"));
```

참 쉽죠잉~ (근데 난 이미지 저장하는 방법 한참 찾았어 =_=;;) 까먹지 않으려고 기록.

RGB를 grayscale로 변경하는 공식은 [요기](/notes/2016/01/12/convert-rgb-to-grayscale)
