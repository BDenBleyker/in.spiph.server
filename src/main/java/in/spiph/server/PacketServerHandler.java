/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.server;

import in.spiph.info.packets.base.APacket;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bennett.DenBleyker
 */
@Sharable
public class PacketServerHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext context;
    private List<APacket> readablePackets = new ArrayList();
    final int receiverType;
    final String name;

    public PacketServerHandler(int receiverType) {
        super();
        this.receiverType = receiverType;
        this.name = receiverType + "";
    }
    
    public PacketServerHandler(int receiverType, String name) {
        super();
        this.receiverType = receiverType;
        this.name = name;
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        APacket packet = (APacket) msg;
        System.out.println(packet.getFrom() + ": " + packet.toString());
        switch (receiverType) {
            case 0:
                Server.handleTrackerPacket(this, packet);
                break;
            case 1:
                Server.handleClientPacket(this, packet);
                break;
            default:
                Server.handleServerPacket(this, packet);
        }
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        System.out.println("Channel (" + this.name + ") Active");
        this.context = ctx;
        for (APacket packet : packetsAwaitingSending) {
            ctx.write(packet);
            System.out.println("Sending " + packet);
        }
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause.getMessage().equals("An existing connection was forcibly closed by the remote host")) {
            System.out.println("\n\tClient disconnected");
        } else {
            cause.printStackTrace();
        }
        ctx.close();
    }
    
    List<APacket> packetsAwaitingSending = new ArrayList();
    public void sendPacket(APacket packet) {
        packet.setFrom(Server.name);
        if (context != null) {
            context.writeAndFlush(packet);
            System.out.println("Sending " + packet);
        } else {
            packetsAwaitingSending.add(packet);
        }
    }

    public List<APacket> getPackets() {
        List<APacket> packets = this.readablePackets;
        this.readablePackets = new ArrayList();
        return packets;
    }
}
