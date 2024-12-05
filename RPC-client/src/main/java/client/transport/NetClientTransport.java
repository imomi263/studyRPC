package client.transport;

import core.common.RpcResponse;
import core.protocol.MessageProtocol;

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
