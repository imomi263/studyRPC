package client.transport;

public class NetClientTransportFactory {
    public static NetClientTransport getNetClientTransport(){
        return new client.transport.NettyNetClientTransport();
    }
}
