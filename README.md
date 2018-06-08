## Java NIO

**java nio 简介**

>Java NIO（New IO）是用于Java（来自Java 1.4）的替代IO API，意味着替代标准 Java IO和Java Networking API。
Java NIO提供了与原来IO API不同的工作方式，但是作用和目的是一样的。
NIO支持面向缓冲区的，基于通道的IO操作。
NIO将以更加高效的方式进行文件的读写操作。

**Java NIO与普通IO的主要区别**

io | nio
---|---
面向流|面向缓冲区（buffer，channel）
堵塞io|非堵塞io
-| 选择器

**java nio主要的核心组件**

- 缓冲区 buffer
- 通道 Channels
- 选择器 Selectors


**java nio缓冲区buffer**

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

**java nio通道channel**

- 简介
   >通道（channel）：用户数 据源节点和目标节点的连接。在Java nio中负责缓冲区中的数据传输，
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
    
    简单示例使用通道和缓冲区复制文件：
    
    ```java
     //使用非直接缓冲区
     FileInputStream fis = new FileInputStream("chao.png");
     FileOutputStream fos = new FileOutputStream("chao2.png");
     //1.获取通道
     FileChannel inChannel = fis.getChannel();
     FileChannel outChannel = fos.getChannel();   
     //2.分配指定大小缓冲区
     ByteBuffer buffer = ByteBuffer.allocate(3072);
     //3.将通道的数据存入缓冲区
     while (inChannel.read(buffer)!=-1){  
         //缓冲区切换为读模式
         buffer.flip();
         //4.将缓冲区数据写入通道
         outChannel.write(buffer);  
         buffer.clear();
     }
     outChannel.close();
     inChannel.close();
     fis.close();
     fos.close();
    ```
  
**使用NIO网络编程**
- 堵塞式socket

    >客户端
    ```java
     try {
          //创建连接通道socketChannel
          SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",8080));
          //本地通道，缓冲区，读取本地文件到通道
          FileChannel inChannel = FileChannel.open(Paths.get("test2"),StandardOpenOption.READ);
          ByteBuffer buf = ByteBuffer.allocate(1024);
          //通道数据保存到缓冲区
          while (inChannel.read(buf)!=-1){
              buf.flip();
              //缓冲区数据传输至socket连接通道
              socketChannel.write(buf);
              buf.clear();
            }  
           //关闭连接写入 --> 而不关闭通道
           socketChannel.shutdownOutput();
           //接收服务端反馈
           int len = 0;
           while ((len =socketChannel.read(buf))!=-1){
               buf.flip();
               System.out.println(new String(buf.array(),0,len));
               buf.clear();
            }  
          inChannel.close();  
         socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();  
     }
    ```
    >服务端
    ```java
    try {
            //1获取通道
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            //2绑定连接
            serverSocketChannel.bind(new InetSocketAddress(8080));
            //3获取客户端连接的通道
            SocketChannel socketChannel = serverSocketChannel.accept();
            //创建本地通道，缓冲区接收客户端数据
            FileChannel outChannel = FileChannel.open(Paths.get("test3"),StandardOpenOption.CREATE,StandardOpenOption.WRITE);
            ByteBuffer buf = ByteBuffer.allocate(1024);
            //4.从socketChannel接收客户端数据
            while (socketChannel.read(buf)!=-1){
                buf.flip();
                outChannel.write(buf);
                buf.clear();
            }
            //返回数据给客户端
            buf.put("服务端接收数据成功".getBytes());
            buf.flip();
            socketChannel.write(buf);
            socketChannel.close();
            serverSocketChannel.close();
        } catch (IOException e) {
    
            e.printStackTrace();
    }
    ```
 
[github博客地址](https://xiaoyangw.github.io/2018/05/30/Java-nio/)   
[可查阅官方api文档](https://docs.oracle.com/javase/8/docs/api/java/nio/package-summary.html)