package com.zx.rpc.server.handler;

import core.common.RpcRequest;
import core.common.RpcResponse;
import core.protocol.MessageHeader;
import core.protocol.MessageProtocol;
import core.protocol.MsgStatus;
import core.protocol.MsgType;
import server.cache.LocalServerCache;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 服务端处理请求
 */

@Slf4j
public class RpcRequestHandler extends SimpleChannelInboundHandler<MessageProtocol<RpcRequest>> {

    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000));


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageProtocol<RpcRequest> rpcRequestMessageProtocol) {
        //多线程处理每个请求
        threadPoolExecutor.submit(()->{
            MessageProtocol<RpcResponse> resProtocol = new MessageProtocol<>();
            RpcResponse response=new RpcResponse();
            MessageHeader header=rpcRequestMessageProtocol.getHeader();

            //设置请求头部消息类型为响应
            header.setMsgType(MsgType.RESPONSE.getType());
            try {
                Object result = handle(rpcRequestMessageProtocol.getBody());
                response.setData(result);
                header.setStatus(MsgStatus.SUCCESS.getCode());
                resProtocol.setHeader(header);
                resProtocol.setBody(response);
            } catch (Throwable throwable) {
                header.setStatus(MsgStatus.FAIL.getCode());
                response.setMessage(throwable.toString());
                log.error("process request {} error", header.getRequestId(), throwable);
            }

            // 把数据写回去
            channelHandlerContext.writeAndFlush(resProtocol);
        });
    }

    /**
     * 反射调用该服务上的方法获取数据
     */
    private Object handle(RpcRequest request) {
        try {
            Object bean = LocalServerCache.get(request.getServiceName());
            if (bean == null) {
                throw new RuntimeException(String.format("service not exist: %s !", request.getServiceName()));
            }
            // 反射调用
            Method method = bean.getClass().getMethod(request.getMethod(), request.getParameterTypes());
            return method.invoke(bean, request.getParameters());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
