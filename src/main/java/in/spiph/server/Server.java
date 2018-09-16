/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.server;

import in.spiph.info.Page;
import in.spiph.info.Post;
import in.spiph.info.packets.base.APacket;
import in.spiph.info.packets.base.TestPacket;
import in.spiph.info.packets.client.PagePacket;
import io.netty.channel.ChannelPipeline;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Bennett.DenBleyker
 */
public class Server {

    public static String name = "S" + Math.random() + "S";
    public static Page page = new Page(new ArrayList<Post>(), "ABCD");
    private static PacketServer packetServer;

    public static void main(String[] args) throws Exception {
        System.setProperty("spiphi.port", "4198");
        
        ipList.put(123456789L, "127.0.0.1");
        
        page.getPosts().add(new Post(LocalDateTime.now(), "Hi"));
        page.getPosts().add(new Post(LocalDateTime.now(), "Ho"));
        
        packetServer = new PacketServer();
        
        new Thread(packetServer).start();
    }

    public static void handleClientPacket(ChannelPipeline pipeline, APacket packet) {
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
                String[] dataSplit = packet.getData().toString().split(";");
                long id = Long.valueOf(dataSplit[0]);
                if (packet.getData() instanceof Page) {
                    page = (Page) packet.getData();
                } else if (ipList.containsKey(id)) {
                    PacketServerClient server = new PacketServerClient(pipeline);
                    server.handler.fireUserEventTriggered(packet);
                    try {
                        server.initialize(ipList.get(id));
                    } catch (IOException | InterruptedException ex) {
                        System.err.println("Server closed");
                        ex.printStackTrace();
                    }
                } else {
//                    tracker.sendPacket(new IpPacket(Long.valueOf((String) packet.getData())));
                }
                break;
            default: // ErrorPacket
                System.out.println("Invalid packet id (" + packet.getType() + "): " + packet.toString());
        }
    }

    public static void handleTrackerPacket(ChannelPipeline pipeline, APacket packet) {
        switch (packet.getType()) {
            case 0: // TestPacket
                switch (packet.getData().toString()) {
                    case "Request":
//                        tracker.sendPacket(new TestPacket("Hello!"));
                        break;
                    default:
                        System.out.println("Test Succeeds");
                }
                break;
            case 1: // IpPacket
                String[] pData = packet.getData().toString().split(";");
                Server.addIp(Long.valueOf(pData[0]), pData[1]);
                break;
            default: // ErrorPacket
                System.out.println("Invalid packet id (" + packet.getType() + "): " + packet.toString());
        }
    }

    public static void handleServerPacket(ChannelPipeline pipeline, APacket packet) {
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
                if (packet.getData() instanceof String) {
                    pipeline.fireUserEventTriggered(new PagePacket(page));
                }
                break;
            default: // ErrorPacket
                System.out.println("Invalid packet id (" + packet.getType() + "): " + packet.toString());
        }
    }

    private static Map<Long, String> ipList = new HashMap();

    public static void addIp(long id, String ip) {
        ipList.put(id, ip);
        System.out.println("Learned ip " + ip + " for id " + id);
    }
}
