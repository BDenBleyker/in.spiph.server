/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.server;

import in.spiph.info.packets.handling.PacketHandler;
import in.spiph.info.Page;
import in.spiph.info.packets.base.APacket;
import in.spiph.info.packets.base.TestPacket;
import in.spiph.info.packets.client.PagePacket;
import in.spiph.info.packets.tracker.IpPacket;
import io.netty.channel.ChannelPipeline;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bennett_DenBleyker
 */
public class PacketServerServerHandler extends PacketHandler {

    private final ChannelPipeline associatedClient;

    public PacketServerServerHandler(String from, ChannelPipeline associatedClient) {
        super(from);
        this.associatedClient = associatedClient;
    }

    @Override
    public boolean handleException(Throwable cause) {
        if (cause.getMessage().equals("An existing connection was forcibly closed by the remote host")) {
            Logger.getLogger(Server.class.getName()).log(Level.INFO, "\n\tServer/Tracker disconnected");
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void handlePacket(ChannelPipeline pipeline, APacket packet) {
        switch (packet.getType()) {
            case TestPacket.TYPE_VALUE:
                if (packet.isRequest()) {
                    pipeline.fireUserEventTriggered(new TestPacket("Hello!"));
                } else {
                    Logger.getLogger(Server.class.getName()).log(Level.INFO, "Test Succeeds");
                }
                break;
            case PagePacket.TYPE_VALUE:
                if (packet.getData() instanceof Page) {
                    associatedClient.fireUserEventTriggered(packet);
                    pipeline.close();
                } else {
                    Logger.getLogger(Server.class.getName()).log(Level.WARNING, "Requested a PagePacket, got a request back");
                }
                break;
            default: // ErrorPacket
                Logger.getLogger(Server.class.getName()).log(Level.WARNING, "Invalid packet id ({0}): {1}", new Object[]{packet.getType(), packet.toString()});
        }
    }

    @Override
    public boolean getTestMode() {
        return false;
    }

}
