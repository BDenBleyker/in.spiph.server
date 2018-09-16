/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.server;

import in.spiph.info.packets.handling.PacketServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLException;

/**
 *
 * @author Bennett.DenBleyker
 */
public class PacketServer implements Runnable {

    private static final int PORT = Integer.parseInt(System.getProperty("spiphi.port", "4198"));
    static final boolean SSL = System.getProperty("ssl") != null;

    @Override
    public void run() {
        try {
            System.out.println("Starting PacketServer on port " + PORT);
            
            //Setup SSL
            final SslContext sslCtx;
            if (SSL) {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            } else {
                sslCtx = null;
            }

            //Thread management
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new PacketServerInitializer(sslCtx, new PacketServerHandler(Server.name))) //Initializes
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true); //Prevents shutdown after the first finished packet
                ChannelFuture f = b.bind(PORT).sync() //Hold the thread until the Future finishes binding
                        .channel().closeFuture().sync(); //Hold the thread until the Future closes
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
            
        //
        } catch (SSLException ex) {
            System.err.println("SSL failed to build\n" + ex.getLocalizedMessage());
        } catch (CertificateException ex) {
            System.err.println("Invalid certificate\n" + ex.getLocalizedMessage());
        } catch (InterruptedException ex) {
            System.err.println("ChannelFuture failed to bind or to close\n" + ex.getLocalizedMessage());
        }
    }
}
