/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.server.servercom;

import in.spiph.info.packets.base.APacket;
import in.spiph.info.packets.serializing.PacketDecoder;
import in.spiph.info.packets.serializing.PacketEncoder;
import in.spiph.server.PacketServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import java.util.List;

/**
 *
 * @author Bennett_DenBleyker
 */
public class ServerCom implements Runnable {

    private final int port;
    static final boolean SSL = System.getProperty("ssl") != null;
    private final PacketServerHandler packetHandler = new PacketServerHandler(2, "ServerCom");

    public ServerCom(int port) {
        this.port = port;
    }

    public ServerCom() {
        this.port = Integer.valueOf(System.getProperty("spiphi.server_port", "4196"));
    }

    @Override
    public void run() {
        try {
            System.out.println("Starting ServerCom on port " + this.port);
            final SslContext sslCtx;
            if (SSL) {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            } else {
                sslCtx = null;
            }

            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline p = ch.pipeline();
                                if (sslCtx != null) {
                                    p.addLast(sslCtx.newHandler(ch.alloc()));
                                }
                                p.addLast(new PacketEncoder(), new PacketDecoder(), packetHandler);
                            }
                        })
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);
                ChannelFuture f = b.bind(port).sync().channel().closeFuture().sync();
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendPacket(APacket packet) {
        packetHandler.sendPacket(packet);
    }

    public List<APacket> receivePackets() {
        return packetHandler.getPackets();
    }
}
