package core.codec;

import core.common.RpcRequest;
import core.common.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import core.protocol.MessageHeader;
import core.protocol.MessageProtocol;
import core.protocol.MsgType;
import core.protocol.ProtocolConstants;
import core.serialization.RpcSerialization;
import core.serialization.SerializationFactory;
import core.serialization.SerializationTypeEnum;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {

    /**
     *
     * +---------------------------------------------------------------+
     *      *  | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte|
     *      *  +---------------------------------------------------------------+
     *      *  | 状态 1byte |        消息 ID 8byte     |      数据长度 4byte     |
     *      *  +---------------------------------------------------------------+
     *      *  |                   数据内容 （长度不定）                         |
     *      *  +---------------------------------------------------------------+
     *
     *
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() < ProtocolConstants.HEADER_TOTAL_LEN) {
            return;
        }
        //标记读指针的位置
        in.markReaderIndex();

        short magic = in.readShort();

        if(magic!=ProtocolConstants.MAGIC){
            throw new IllegalArgumentException("magic number is illegal"+magic);
        }

        byte version = in.readByte();
        byte serializeType = in.readByte();
        byte msgType = in.readByte();
        byte status = in.readByte();

        //读取请求id 8字节长度
        CharSequence requestId = in.readCharSequence(ProtocolConstants.REQ_LEN, StandardCharsets.UTF_8);

        //数据长度
        int dataLength = in.readInt();

        if(in.readableBytes()<dataLength){
            // 可读的数据长度小于 请求体长度 直接丢弃并重置 读指针位置
            in.resetReaderIndex();
            return;
        }

        byte[] data = new byte[dataLength];
        in.readBytes(data);

        //获取报文类型
        MsgType msgTypeEnum = MsgType.findByType(msgType);
        if (msgTypeEnum == null) {
            return;
        }

        MessageHeader header = new MessageHeader();
        header.setMagic(magic);
        header.setVersion(version);
        header.setSerialization(serializeType);
        header.setStatus(status);
        header.setRequestId(String.valueOf(requestId));
        header.setMsgType(msgType);
        header.setMsgLen(dataLength);

        //获取序列化对象
        RpcSerialization rpcSerialization = SerializationFactory.getRpcSerialization(SerializationTypeEnum.parseByType(serializeType));

        switch (msgTypeEnum){
            case REQUEST:
                RpcRequest request=rpcSerialization.deserialize(data, RpcRequest.class);
                if(request!=null){
                    MessageProtocol<RpcRequest> protocol=new MessageProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(request);
                    out.add(protocol);
                }
                break;

            case RESPONSE:
                RpcResponse response = rpcSerialization.deserialize(data, RpcResponse.class);
                if (response != null) {
                    MessageProtocol<RpcResponse> protocol = new MessageProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(response);
                    out.add(protocol);
                }
                break;
        }


    }
}
