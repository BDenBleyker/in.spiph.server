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
import in.spiph.info.packets.serializing.PacketEncoder;
import io.netty.channel.ChannelPipeline;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bennett_DenBleyker
 */
public class PacketServerClientHandler extends PacketHandler {

    private final ChannelPipeline associatedClient;

    public PacketServerClientHandler(String from, ChannelPipeline associatedClient) {
        super(from);
        this.associatedClient = associatedClient;
    }

    @Override
    public boolean handleException(Throwable cause) {
        return false;
    }

    @Override
    public void handlePacket(ChannelPipeline pipeline, APacket packet) {
        switch (packet.getFrom().charAt(0)) {
            case 'S': //Server
                switch (packet.getType()) {
                    case 0: // TestPacket
                        switch (packet.getData().toString()) {
                            case "Request":
                                pipeline.fireUserEventTriggered(new TestPacket("Hello!"));
                                break;
                            default:
                                System.out.println("Test Succeeds");
                        }
                        break;
                    case 2: // PagePacket
                        if (packet.getData() instanceof Page) {
                            associatedClient.fireUserEventTriggered(packet);
                            pipeline.close();
                        } else {
                            System.out.println("Requested a PagePacket, got a request back");
                        }
                        break;
                    default: // ErrorPacket
                        System.out.println("Invalid packet id (" + packet.getType() + "): " + packet.toString());
                }
                break;
            case 'T': //Tracker
                switch (packet.getType()) {
                    case 0: // TestPacket
                        switch (packet.getData().toString()) {
                            case "Request":
                                pipeline.fireUserEventTriggered(new TestPacket("Hello!"));
                                break;
                            default:
                                System.out.println("Test Succeeds");
                        }
                        break;
                    case 1: // IpPacket
                        String[] pData = packet.getData().toString().split(";");
                        Server.addIp(pData[0], pData[1]);
                        pipeline.close();
                        break;
                    default: // ErrorPacket
                        System.out.println("Invalid packet id (" + packet.getType() + "): " + packet.toString());
                }
                break;
            default:
                System.out.println("Packet received from unknown (" + packet.getFrom() + "). Cannot handle.");
        }

    }

    @Override
    public boolean getTestMode() {
        return false;
    }

}
