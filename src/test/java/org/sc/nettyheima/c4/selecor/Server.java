package org.sc.nettyheima.c4.selecor;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;

import static org.sc.nettyheima.c1.ByteBufferUtil.debugRead;

/**
 * @author shi kang
 * @email shikang1008@outlook.com
 * @date 2024/12/23
 * @description Brief description of the class
 */
@Slf4j
public class Server {
    //使用nio 来理解elector模式 ,单线程      虽然可以处理多个连接和数据处理 但是一直在循环,浪费性能 cpu空轮询
    public static void main(String[] args) {
        try {
            //1. 创建selector
            Selector selector = Selector.open();
            //2. 创建ServerSocketChannel 绑定端口
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress(8080));
            ssc.configureBlocking(false);
            //3. 将ServerSocketChannel注册到selector上
            //SelectionKey 可以得到发生的事件类型和 对应的channel(ServerSocketChannel对象相关的SocketChannel和对应事件)
            SelectionKey sscKey = ssc.register(selector, 0, null);
            //4. 监听accept事件
            sscKey.interestOps(SelectionKey.OP_ACCEPT);
            log.debug("sscKey :{}", sscKey);
            while (true) {
                //5. select是阻塞方法,有事件才不阻塞
                selector.select();
                // 6. 处理事件
                //7. 获取所有事件
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    log.debug("key :{}", key);
                    if (key.isAcceptable()) {
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                        SocketChannel sc = channel.accept();
                        sc.configureBlocking(false);
                        SelectionKey scKey = sc.register(selector, 0, null);
                        scKey.interestOps(SelectionKey.OP_READ);

                        //取消事件    ,防止下次select时重复处理
                        //key.cancel();
                        log.debug("scKey :{}", scKey);
                    } else if (key.isReadable()) {
                        try {
                            SocketChannel sc = (SocketChannel) key.channel();
                            ByteBuffer read = ByteBuffer.allocate(16);
                            int read1 = sc.read(read);

                            if (read1 == -1) {
                                key.cancel(); //取消注册
                            } else {
                                read.flip();
                                debugRead(read);
                                read.clear();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            key.cancel(); //取消注册
                        }
                    }

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
