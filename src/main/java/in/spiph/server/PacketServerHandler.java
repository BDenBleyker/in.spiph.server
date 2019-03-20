/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.server;

import in.spiph.info.packets.handling.PacketHandler;
import in.spiph.info.packets.base.APacket;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelPipeline;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bennett.DenBleyker
 */
@Sharable
public class PacketServerHandler extends PacketHandler {

    public PacketServerHandler(String from) {
        super(from);
    }

    @Override
    public boolean handleException(Throwable cause) {
        if (cause.getMessage().equals("An existing connection was forcibly closed by the remote host")) {
            Logger.getLogger(Server.class.getName()).log(Level.INFO, "\n\tClient disconnected");
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void handlePacket(ChannelPipeline pipeline, APacket packet) {
        switch (packet.getFrom().charAt(0)) {
            case 'C': //Client
                Server.handleClientPacket(pipeline, packet);
                break;
            case 'S': //Server
                Server.handleServerPacket(pipeline, packet);
                break;
            default:
                Logger.getLogger(Server.class.getName()).log(Level.WARNING, "Packet received from unknown ({0}). Cannot handle.", packet.getFrom());
        }
    }
    
    @Override
    public boolean getTestMode() {
        return false;
    }
    
}
