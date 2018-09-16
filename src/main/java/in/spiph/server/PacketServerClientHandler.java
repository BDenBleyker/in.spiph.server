/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.server;

import in.spiph.info.packets.handling.PacketHandler;
import in.spiph.info.Page;
import in.spiph.info.packets.base.APacket;
import io.netty.channel.ChannelPipeline;

/**
 *
 * @author Bennett_DenBleyker
 */
public class PacketServerClientHandler extends PacketHandler {

    private ChannelPipeline associatedClient;

    public PacketServerClientHandler(String from, ChannelPipeline associatedClient) {
        super(from);
        this.associatedClient = associatedClient;
    }

    @Override
    public boolean handleException(Throwable cause) {
        return false;
    }

    @Override
    public void handlePacket(ChannelPipeline associatedServer, APacket packet) {
        if (packet.getData() instanceof Page) {
            associatedClient.fireUserEventTriggered(packet);
            associatedServer.close();
        } else {
            System.out.println("Strange packet......Requested a PagePacket, got a " + packet.getType());
        }
    }

    @Override
    public boolean getTestMode() {
        return false;
    }

}
