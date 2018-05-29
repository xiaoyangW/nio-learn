# nio-learn
**1.  java nio 简介**

Java NIO（New IO）是用于Java（来自Java 1.4）的替代IO API，意味着替代标准 Java IO和Java Networking API。Java NIO提供了与原来IO API不同的工作方式，但是作用和目的是一样的。NIO支持面向缓冲区的，基于通道的IO操作。NIO将以更加高效的方式进行文件的读写操作。

**2. Java NIO与普通IO的主要区别**

io | nio
---|---
面向流|面向缓冲区（buffer，channel）
堵塞io|非堵塞io
-| 选择器

**3.java nio主要的核心组件**
- 通道 Channels
- 缓冲区 buffer
- 选择器 Selectors







![可查阅api文档](https://docs.oracle.com/javase/8/docs/api/java/nio/package-summary.html)