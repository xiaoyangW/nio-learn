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
    
- 非堵塞式socket
    >客户端
    ```java
    //1.获取通道
    SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",8080));
    //2.设置为非堵塞模式
    socketChannel.configureBlocking(false);
    ByteBuffer buf = ByteBuffer.allocate(1024);
    //3.发送数据给服务端
    //控制台输入数据
    Scanner scanner = new Scanner(System.in);
    while (scanner.hasNext()){
        String msg = scanner.next();
        buf.put(msg.getBytes());
        buf.flip();
        socketChannel.write(buf);
        buf.clear();
    }
    //4.关闭连接
    socketChannel.close();
    ```
    >服务端
    ```java
    //1.获取通道
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    //2.切换到非堵塞模式
    serverSocketChannel.configureBlocking(false);
    //3.绑定端口号
    serverSocketChannel.bind(new InetSocketAddress(8080));
    //4.获取选择器
    Selector selector = Selector.open();
    //5.将通道注册到选择器上，并且指定“监听接收事件”
    serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);
    //6轮询式的获取选择器上已经‘准备就绪’的事件
    while (selector.select()>0){
        //7 。获取当前选择器中所有注册的"选择健（已就绪的监听事件）"
        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
        while (iterator.hasNext()){
            //8.获取“准备就绪”的事件
            SelectionKey selectionKey = iterator.next();
             //9.判断具体事件，就绪
            if (selectionKey.isAcceptable()){
                //10.接收就绪，获取客户端连接
                SocketChannel socketChannel = serverSocketChannel.accept();
                //11,切换到非堵塞模式
                socketChannel.configureBlocking(false);
                //12.将客户端通道注册到选择器上
                socketChannel.register(selector,SelectionKey.OP_READ);
            }else if (selectionKey.isReadable()){
                //获取当前选择器上“读就绪”状态的通道
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

                ByteBuffer buffer = ByteBuffer.allocate(1024);
                //读取客户端传过来的数据
                int len = 0;
                while ((len = socketChannel.read(buffer))>0){
                  buffer.flip();
                  System.out.println(new String(buffer.array(),0,len));
                  buffer.clear();  
              }
            }
            //取消选择键selectionKey
            iterator.remove();
        }
    }
    ```
- UDP

    >UDP 全称 User Datagram Protocol, 与 TCP 同是在网络模型中的传输层的协议。 
    
    UDP 的主要特点是：
     - 无连接的，即发送数据之前不需要建立连接，因此减少了开销和发送数据之前的时延。
     不保证可靠交付，因此主机不需要为此复杂的连接状态表
     - 面向报文的，意思是 UDP 对应用层交下来的报文，既不合并，也不拆分，而是保留这些报文的边界，在添加首部后向下交给 IP 层。
     - 没有阻塞控制，因此网络出现的拥塞不会使发送方的发送速率降低。
     - 支持一对一、一对多、多对一和多对多的交互通信，也即是提供广播和多播的功能。
     - 首部开销小，首部只有 8 个字节，分为四部分。
     
    > UDP 的常用场景
     
     - 名字转换（DNS）
     - 文件传送（TFTP）
     - 路由选择协议（RIP）
     - IP 地址配置（BOOTP，DHTP）
     - 网络管理（SNMP）
     - 远程文件服务（NFS）
     - IP 电话
     - 流式多媒体通信

    >客户端
   ```java
        DatagramChannel datagramChannel = DatagramChannel.open();
        
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String msg = scanner.next();
            buffer.put(msg.getBytes());
            buffer.flip();
            datagramChannel.send(buffer,new InetSocketAddress("127.0.0.1",8080));
            buffer.clear();
        }
        datagramChannel.close();
    ```
 
     >服务端
     ```java
      DatagramChannel datagramChannel = DatagramChannel.open();
      datagramChannel.configureBlocking(false);
      datagramChannel.bind(new InetSocketAddress(8080));
      Selector selector = Selector.open();
      datagramChannel.register(selector,SelectionKey.OP_READ);
      while (selector.select()>0){
          Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
          while (iterator.hasNext()){
              SelectionKey selectionKey = iterator.next();
      
              if (selectionKey.isReadable()){
      			ByteBuffer buf = ByteBuffer.allocate(1024);
      			datagramChannel.receive(buf);
      			System.out.println(new String(buf.array(),0,buf.position()));
      			buf.clear();
              }
          }
          iterator.remove();
      }
  ```
 
[个人博客地址](http://wxiaoyang.top/2018/05/30/Java-nio/)  
[CSDN博客地址](https://blog.csdn.net/qq_15144655/article/details/80649343)  
[官方api文档](https://docs.oracle.com/javase/8/docs/api/java/nio/package-summary.html)