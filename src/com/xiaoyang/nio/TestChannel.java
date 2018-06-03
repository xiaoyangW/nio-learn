package com.xiaoyang.nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author WXY
 *
 * 1.通道（channel）：用户数据源节点和目标节点的连接。在Java nio中负责缓冲区中的数据传输，
 *      channel本身不存储数据，因此需要配合缓冲区进行传输
 * 2.通道主要实现类
 *  java.nio.channels.Channel 接口：
 *      |--FileChannel
 *      |--SocketChannel
 *      |--ServerSocketChannel
 *      |--DatagramChannel
 * 3.获取通的方式
 *  (1)Java针对支持通道的类提供getChannel()方法
 *  本地IO：
 *      FileInputStream/FileOutputStream
 *      RandomAccessFile
 *  网络IO：
 *      Socket
 *      ServerSocket
 *      DatagramSocket
 *  (2)在Java1.7中的NIO.2针对各个通道提供了静态方法open()
 *  (3)在Java1.7中的NIO.2的Files工具类的newByteChannel()
 * 4.通道之间的数据传输
 *  transferTo
 *  transferFrom
 *
 * 5.分散（scatter）和聚集（gather）
 *  分散读取（scatter reads）:将通道的数据分散到多个缓冲区中
 *  聚集写入（gather writes）:将多个缓冲区的数据聚集到通道中
 *
 * 6.字符集：charset
 *  编码：字符串-->字节数组
 *  解码：字节数组-->字符串
 */
public class TestChannel {

    public static void main(String[] args){
        try {
            charsetTest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void charsetTest() throws IOException {
        Charset charset = Charset.forName("GBK");
        //获取编码器
        CharsetEncoder encoder = charset.newEncoder();
        //获取解码器
        CharsetDecoder decoder = charset.newDecoder();
        CharBuffer charBuffer = CharBuffer.allocate(1024);
        charBuffer.put("字符串-->字节数组");
        charBuffer.flip();
        //编码
        ByteBuffer bb = encoder.encode(charBuffer);
        for (int i = 0; i < 17; i++) {
            System.out.println(bb.get());
        }
        //解码
        bb.flip();
        CharBuffer cb =decoder.decode(bb);
        System.out.println(cb.toString());
        System.out.println("--------------------");
        Charset ut = Charset.forName("UTF-8");
        bb.flip();
        CharBuffer cb2 =ut.decode(bb);
        System.out.println(cb2.toString());


    }

    /**
     * 分散读取和聚集写入测试代码
     * @throws IOException io
     */
    private static void test4() throws IOException {
        RandomAccessFile raf = new RandomAccessFile("test","rw");
        //1获取通道
        FileChannel channel=raf.getChannel();
        //2创建缓冲区
        ByteBuffer b1= ByteBuffer.allocate(10);
        ByteBuffer b2 = ByteBuffer.allocate(1024);
        //分散读取
        ByteBuffer[] bs = new ByteBuffer[]{b1,b2};
        channel.read(bs);
        for (ByteBuffer b:bs
             ) {
            b.flip();
        }
        System.out.println(new String(bs[0].array(),0,bs[0].limit()));
        System.out.println("----------");
        System.out.println(new String(bs[1].array(),0,bs[1].limit()));
        //聚集写入
        RandomAccessFile rafw = new RandomAccessFile("test2","rw");
        FileChannel wc =  rafw.getChannel();
        wc.write(bs);
    }

    /**
     * 使用直接缓冲区完成文件的复制（内存映射文件）
     * @throws IOException io
     */
    private static void test3() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("chao.png"),StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("chao4.png"),StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE);

        //内存映射文件
        MappedByteBuffer inMappedBuf =  inChannel.map(FileChannel.MapMode.READ_ONLY,0,inChannel.size());
        MappedByteBuffer outMappedBuf =  outChannel.map(FileChannel.MapMode.READ_WRITE,0,inChannel.size());
        //直接对
        byte[] bytes = new byte[inMappedBuf.limit()];
        inMappedBuf.get(bytes);
        outMappedBuf.put(bytes);
        inChannel.close();
        outChannel.close();
    }

    /**
     * 通道之间的数据传输（直接缓冲区）
     * @throws IOException io
     */
    private static void test2() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("chao.png"),StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("chao3.png"),StandardOpenOption.WRITE,StandardOpenOption.CREATE);
        inChannel.transferTo(0,inChannel.size(),outChannel);
        //outChannel.transferFrom(inChannel,0,inChannel.size());

        inChannel.close();
        outChannel.close();
    }

    /**
     * 非直接缓冲区
     * @throws IOException io
     */
    private static void test1() throws IOException {
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
    }

}
