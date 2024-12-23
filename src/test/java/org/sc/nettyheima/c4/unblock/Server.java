package org.sc.nettyheima.c4.unblock;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import static org.sc.nettyheima.c1.ByteBufferUtil.debugRead;

/**
 * @author shi kang
 * @email shikang1008@outlook.com
 * @date 2024/12/23
 * @description Brief description of the class
 */
@Slf4j
public class Server {
    //使用nio 来理解非阻塞模式 ,单线程      虽然可以处理多个连接和数据处理 但是一直在循环,浪费性能 cpu空轮询
    public static void main(String[] args) {
        try {
            ByteBuffer read = ByteBuffer.allocate(16);

            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.bind(new InetSocketAddress(8080));
            log.debug("connecting...");
            ArrayList<SocketChannel> channels = new ArrayList<>();
            while (true) {

                //非阻塞模式 accept方法 不是阻塞方法  没有连接返回null
                SocketChannel sc = ssc.accept();
                if (sc != null) {
                    log.debug("connected {}", sc);
                    sc.configureBlocking(false); //设置非阻塞模式 read方法不是阻塞方法
                    channels.add(sc);
                }

                for (SocketChannel channel : channels) {
                    int read1 = channel.read(read);//读取数据客户端没发送数返回 0
                    if (read1 > 0) {
                        log.debug("before read... {}", channel);
                        read.flip();
                        debugRead(read);
                        read.clear();
                        log.debug("after read... {}", channel);
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
