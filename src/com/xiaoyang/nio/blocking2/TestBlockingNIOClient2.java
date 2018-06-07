package com.xiaoyang.nio.blocking2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author WXY
 *  使用nio实现网络通信（堵塞式）
 *  交互数据
 */
public class TestBlockingNIOClient2 {

    public static void main(String[] args){
        try {
            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",8080));
            //读取文件到通道
            FileChannel inChannel = FileChannel.open(Paths.get("test2"),StandardOpenOption.READ);
            ByteBuffer buf = ByteBuffer.allocate(1024);
            while (inChannel.read(buf)!=-1){
                buf.flip();
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
    }

}
