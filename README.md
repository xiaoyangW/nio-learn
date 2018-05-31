# nio-learn
**1.  java nio 简介**

>Java NIO（New IO）是用于Java（来自Java 1.4）的替代IO API，意味着替代标准 Java IO和Java Networking API。
Java NIO提供了与原来IO API不同的工作方式，但是作用和目的是一样的。
NIO支持面向缓冲区的，基于通道的IO操作。
NIO将以更加高效的方式进行文件的读写操作。

**2. Java NIO与普通IO的主要区别**

io | nio
---|---
面向流|面向缓冲区（buffer，channel）
堵塞io|非堵塞io
-| 选择器

**3.java nio主要的核心组件**

- 缓冲区 buffer
- 通道 Channels
- 选择器 Selectors


**4.java nio缓冲区buffer**

- 简介

  > Buffer是数据的容器，在nio中负责数据的存取，java为不同数据类型提供了相对应的缓冲区类型
   如：ByteBuffer、CharBuffer、ShortBuffer、IntBuffer、LongBuffer、FloatBuffer
   、DoubleBuffer 等。
   
- Buffer的基本使用
    
    >通过allocate()方法获取缓冲区，put()方法存入数据到缓冲区，get()方法获取缓冲区中的数据
    ```java
    String temp = "abcdefg";
    //通过allocate()方法获取指定大小的缓冲区
    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    //使用put()方法将数据添加到缓冲区
    byteBuffer.put(temp.getBytes());
    //缓冲区切换读取数据模式
    byteBuffer.flip();
    //获取缓冲区数据
    byte[] dst = new byte[byteBuffer.limit()];
    //使用get()方法获取数据到dst中
    byteBuffer.get(dst);
    System.out.println(new String(dst,0,dst.length));
    ```
- Buffer的核心属性
    
     * capacity:容量，表示缓冲区中最大存储数据的容量，一旦声明不能改变。
     *  limit:界限，表示缓冲区中可以操作数据的大小。（limit后数据不能进行读写）
     *  position:位置，表示缓冲区中正在操作数据的位置
     *  mark: 标记，表示记录当前position的位置，可通过reset()恢复到mark的位置    
    
    mark <= position <= limit <= capacity，
    属性的各种状态的值可查看TestBuffer.java中的测试代码
    
- 直接缓冲区与非直接缓冲区

    * 非直接缓冲区：通过allocate()分配缓冲区，缓冲区建立在jvm中。
    * 直接缓冲区：通过allocateDirect()方法创建缓冲区，缓冲区建立在系统物理内存中。

**4.java nio通道channel**

- 简介
   >通道（channel）：用户数据源节点和目标节点的连接。在Java nio中负责缓冲区中的数据传输，
    channel本身不存储数据，因此需要配合缓冲区进行传输,实现java.nio.channels.Channel接口
    ，主要实现类有FileChannel、SocketChannel、ServerSocketChannel、DatagramChannel等。

- 获取通的方式
    * (1)java针对支持通道的类提供getChannel()方法
            本地IO有
          FileInputStream/FileOutputStream
          RandomAccessFile，
          网络IO有
          Socket
          、ServerSocket
          、DatagramSocket
    *  (2)在Java1.7中的NIO.2针对各个通道提供了静态方法open()
    *  (3)在Java1.7中的NIO.2的Files工具类的newByteChannel()
    
[可查阅官方api文档](https://docs.oracle.com/javase/8/docs/api/java/nio/package-summary.html)