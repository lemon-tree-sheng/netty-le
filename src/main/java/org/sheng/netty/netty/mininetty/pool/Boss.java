package org.sheng.netty.netty.mininetty.pool;

import java.nio.channels.ServerSocketChannel;

/**
 * @author shengxingyue
 */
public interface Boss {

    /**
     * 加入一个新的ServerSocket
     *
     * @param serverChannel
     */
    void registerAcceptChannelTask(ServerSocketChannel serverChannel);
}
