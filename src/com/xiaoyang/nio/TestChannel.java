package com.xiaoyang.nio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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
 */
public class TestChannel {

    public static void main(String[] args){
        try {
            test2();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
