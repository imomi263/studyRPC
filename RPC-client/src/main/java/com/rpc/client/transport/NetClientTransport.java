package com.rpc.client.transport;

import com.rpc.core.common.RpcResponse;
import com.rpc.core.protocol.MessageProtocol;

/**
 * 网络传输层
 */
public interface NetClientTransport {
    /**
     * 发送请求
     * @param metadata
     * @return
     * @throws Exception
     */
    MessageProtocol<RpcResponse> sendRequest(RequestMetadata metadata) throws Exception;
}
