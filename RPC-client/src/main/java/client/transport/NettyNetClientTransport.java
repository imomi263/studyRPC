package client.transport;

import client.cache.LocalRpcResponseCache;
import client.handler.RpcResponseHandler;
import core.codec.RpcDecoder;
import core.codec.RpcEncoder;
import core.common.RpcRequest;
import core.common.RpcResponse;
import core.protocol.MessageProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyNetClientTransport implements NetClientTransport {
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;
    private final RpcResponseHandler handler;

    public NettyNetClientTransport() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        handler=new RpcResponseHandler();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(
                        new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                socketChannel.pipeline()
                                        .addLast(new RpcDecoder())
                                        .addLast(handler)
                                        .addLast(new RpcEncoder<>());
                            }
                        }
                );

    }

    @Override
    public MessageProtocol<RpcResponse> sendRequest(RequestMetadata metadata) throws Exception {
        MessageProtocol<RpcRequest> protocol=metadata.getProtocol();

        RpcFuture<MessageProtocol<RpcResponse>> future=new RpcFuture<>();
        LocalRpcResponseCache.add(protocol.getHeader().getRequestId(),future);

        ChannelFuture channelFuture=bootstrap.connect(metadata.getAddress(),metadata.getPort()).sync();

        channelFuture.addListener((ChannelFutureListener) arg0 -> {
            if (channelFuture.isSuccess()) {
                log.info("connect rpc server {} on port {} success.", metadata.getAddress(), metadata.getPort());
            } else {
                log.error("connect rpc server {} on port {} failed.", metadata.getAddress(), metadata.getPort());
                channelFuture.cause().printStackTrace();
                eventLoopGroup.shutdownGracefully();
            }
        });


        // 写入请求数据
        channelFuture.channel().writeAndFlush(protocol);
        System.out.println("=====数据写入完成=========");
        //这里的响应会被future阻塞 响应后返回
        return metadata.getTimeout()!=null ? future.get(metadata.getTimeout(), TimeUnit.MILLISECONDS) :future.get();
    }
}
