/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.server.trackercom;

import in.spiph.info.packets.base.APacket;
import in.spiph.info.packets.serializing.PacketDecoder;
import in.spiph.info.packets.serializing.PacketEncoder;
import in.spiph.server.PacketServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Bennett.DenBleyker
 */
public class TrackerCom {
    static final boolean SSL = System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("tracker_host", "127.0.0.1");
    static final int PORT = Integer.valueOf(System.getProperty("spiphi.tracker_port", "4197"));;

    private PacketServerHandler handler = new PacketServerHandler(0, "TrackerCom");

    boolean started = false;

    public void initialize() throws IOException, InterruptedException {
        if (!started) {
            System.out.println("Starting on port " + PORT);

            //final SslContext sslCtx;
            //if (SSL) {
            //    sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            //} else {
            //    sslCtx = null;
            //}
            EventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(group)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline p = ch.pipeline();
                                //if (sslCtx != null) {
                                //    p.addLast(sslCtx.newHandler(ch.alloc(), HOST, PORT));
                                //}
                                p.addLast(
                                        new PacketEncoder(),
                                        new PacketDecoder(),
                                        handler);
                            }
                        });

                ChannelFuture f = b.connect(HOST, PORT).sync().channel().closeFuture().sync();
            } finally {
                group.shutdownGracefully();
            }
            started = true;
        }
    }

    public void sendPacket(APacket packet) {
        handler.sendPacket(packet);
    }

    public List<APacket> receivePackets() {
        return handler.getPackets();
    }
}
