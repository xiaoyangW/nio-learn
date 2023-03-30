package com.xiaoyang.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

/**
 * @author WXY
 * Java nio 管道Pipe测试
 * <p>
 * Java nio管道是两个线程之间的单向数据连接
 * Pipe有一个source通道和一个sink通道，数据会被写入到sink通道，从source通道读取
 */
public class testPipe {

    public static void main(String[] args) {

        try {
            //1.获取管道
            Pipe pipe = Pipe.open();
            //2.将缓冲区数据写入管道
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put("aaaaaaaaaaa".getBytes());
            Pipe.SinkChannel sinkChannel = pipe.sink();
            buffer.flip();
            sinkChannel.write(buffer);

            //3.读取缓冲区数据
            Pipe.SourceChannel sourceChannel = pipe.source();
            ByteBuffer buffer1 = ByteBuffer.allocate(1024);
            sourceChannel.read(buffer1);
            System.out.println(new String(buffer1.array(), 0, buffer1.position()));
            buffer.clear();
            buffer1.clear();
            sinkChannel.close();
            sourceChannel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
