package udpserver;

import config.Config;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;


public class UDPServer implements Runnable{

    private static int MAX_SFLOW_PKT_SIZ = 65536;
    private int port;
    boolean is_running;

    public UDPServer(int port) {
        this.port = port;
        is_running = true;
    }

    @Override
    public void run() {
        DatagramChannel channel;

        try {
            channel = DatagramChannel.open();
            channel.socket().bind(new InetSocketAddress(port));
        } catch (Exception e) {
            Config.LOG_INFO(e.getMessage());
            return;
        }

        while(is_running) {
            ByteBuffer buf = ByteBuffer.allocate(MAX_SFLOW_PKT_SIZ);
            buf.clear();
            InetSocketAddress socketAddress;
            try {
                socketAddress = (InetSocketAddress)channel.receive(buf);
                buf.flip();

            } catch (IOException e) {
                Config.LOG_INFO(e.getMessage());
                continue;
            }

            SFlowDatagram datagram = new SFlowDatagram(buf, socketAddress.getAddress());

            try {
                datagram.process();
            }catch (Exception e) {
                Config.LOG_ERROR(e.getMessage());
            }
        }
    }
}