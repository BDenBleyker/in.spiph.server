/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.server.localclientcom;

import in.spiph.server.PacketServerHandler;
import in.spiph.info.packets.serializing.PacketDecoder;
import in.spiph.info.packets.serializing.PacketEncoder;
import in.spiph.info.packets.base.APacket;
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
import java.security.cert.CertificateException;
import java.util.List;
import javax.net.ssl.SSLException;

/**
 *
 * @author Bennett.DenBleyker
 */
public class LocalClientCom implements Runnable {

    private final int port;
    static final boolean SSL = System.getProperty("ssl") != null;
    public final PacketServerHandler packetHandler = new PacketServerHandler(1, "LocalClientCom");

    public LocalClientCom(int port) {
        this.port = port;
    }

    public LocalClientCom() {
        this.port = Integer.valueOf(System.getProperty("spiphi.client_port", "4198"));
    }

    @Override
    public void run() {
        try {
            System.out.println("Starting LocalClientCom on port " + this.port);
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
        } catch (InterruptedException | CertificateException | SSLException ex) {
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
