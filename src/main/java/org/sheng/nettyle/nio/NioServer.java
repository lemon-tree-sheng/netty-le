package org.sheng.nettyle.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author shengxingyue, created on 2018/1/18
 */
@Slf4j
public class NioServer {

    /**
     * 通道监控器
     */
    private Selector selector;

    NioServer() throws IOException {
        initServer(8000);
    }

    public void initServer(int port) throws IOException {
        // 初始化服务端 channel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));

        // 监控器绑定服务端 channel
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        log.info("初始化服务端成功");
    }

    /**
     * 服务端监听
     *
     * @throws IOException
     */
    public void listen() throws IOException {
        while (true) {
            log.info("服务端监听中");
            // 阻塞监听注册事件
            selector.select();

            // 监听到注册事件
            Iterator iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = (SelectionKey) iterator.next();
                // 移除监听到的事件
                iterator.remove();
                // 处理监听到的事件
                handleSelectionKey(selectionKey);
            }
        }
    }

    /**
     * 处理监听到的事件
     *
     * @param selectionKey
     * @throws IOException
     */
    private void handleSelectionKey(SelectionKey selectionKey) throws IOException {
        if (selectionKey.isAcceptable()) {
            // 处理读事件
            handleAccept(selectionKey);
        } else if (selectionKey.isReadable()) {
            // 处理客户端连接事件
            handleRead(selectionKey);
        }
    }

    /**
     * 处理读事件
     * 事件处理完之后不再阻塞读
     *
     * @param selectionKey
     * @throws IOException
     */
    private void handleRead(SelectionKey selectionKey) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        // 读是读的客户端 channel, 所以强转成客户端 channel
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        int read = socketChannel.read(byteBuffer);
        if (read > 0) {
            byte[] bytes = byteBuffer.array();
            log.info(new String(bytes).trim());

            // 会写到客户端
            socketChannel.write(ByteBuffer.wrap("服务端已经接收到消息".getBytes()));
        } else {
            socketChannel.close();
        }
    }

    /**
     * 处理客户端连接事件
     *
     * @param selectionKey
     * @throws IOException
     */
    private void handleAccept(SelectionKey selectionKey) throws IOException {
        log.info("客户端连接成功");
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();

        // 客户端 channel 上注册读事件
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    public static void main(String[] args) throws IOException {
        NioServer nioServer = new NioServer();
        nioServer.listen();
    }
}
