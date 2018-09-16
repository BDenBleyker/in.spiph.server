/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.server;

import in.spiph.info.packets.handling.PacketClientInitializer;
import in.spiph.info.packets.handling.PacketHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import java.io.IOException;

/**
 *
 * @author Bennett_DenBleyker
 */
public class PacketServerClient {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.valueOf(System.getProperty("spiphi.port", "4198"));
    
    private int portskew = 0; //Cannot use the same port because they are on the same machine
    public PacketHandler handler;
    
    public PacketServerClient(ChannelPipeline associatedClient) {
         handler = new PacketServerClientHandler(Server.name, associatedClient);
    }
    
    public PacketServerClient(ChannelPipeline associatedClient, int port) { //Cannot use the same port because they are on the same machine
         handler = new PacketServerClientHandler(Server.name, associatedClient);
         this.portskew = port;
    }

    public void initialize(String ip) throws IOException, InterruptedException {
        System.out.println("Starting PacketServerClient on port " + ((portskew > 0) ? portskew : PORT)); //Cannot use the same port because they are on the same machine

        //Setup SSL
        final SslContext sslCtx;
        if (SSL) {
            sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }
        
        //Thread management
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new PacketClientInitializer(sslCtx, ip, ((portskew > 0) ? portskew : PORT), handler)); //Cannot use the same port because they are on the same machine

            ChannelFuture f = b.connect(ip, ((portskew > 0) ? portskew : PORT)).sync().channel().closeFuture().sync(); //Cannot use the same port because they are on the same machine
        } finally {
            group.shutdownGracefully();
        }
    }
}
