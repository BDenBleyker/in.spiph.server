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
import in.spiph.info.packets.tracker.IpPacket;
import io.netty.channel.ChannelPipeline;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Bennett.DenBleyker
 */
public class Server {

    public static String name = "S" + Math.random() + "S";
    public static Page page = new Page(new ArrayList<Post>(), "ABCD");
    public static List<String> trackerIps = new ArrayList();
    private static final Map<String, String> IPLIST = new HashMap();

    public static void main(String[] args) throws Exception {
        System.setProperty("spiphi.port", "4198");
        
        trackerIps.add("127.0.0.1");
        
        page.getPosts().add(new Post(LocalDateTime.now(), "Hi"));
        page.getPosts().add(new Post(LocalDateTime.now(), "Ho"));
        
        new Thread(new PacketServer()).start();
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
                if (packet.getData() instanceof Page) {
                    page = (Page) packet.getData();
                } else if (IPLIST.containsKey(dataSplit[0])) {
                    PacketServerClient server = new PacketServerClient(pipeline);
                    server.handler.fireUserEventTriggered(packet);
                    try {
                        server.initialize(IPLIST.get(dataSplit[0]));
                    } catch (IOException | InterruptedException ex) {
                        System.err.println("Server closed");
                        ex.printStackTrace();
                    }
                } else {
                    PacketServerClient tracker = new PacketServerClient(pipeline, 4199); //Cannot use the same port because they are on the same machine
                    tracker.handler.fireUserEventTriggered(new IpPacket((String) packet.getData()));
                    try {
                        tracker.initialize(trackerIps.get(0));
                    } catch (IOException | InterruptedException ex) {
                        System.err.println("Tracker closed");
                        ex.printStackTrace();
                    }
                    pipeline.fireUserEventTriggered(packet);
                }
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

    public static void addIp(String id, String ip) {
        IPLIST.put(id, ip);
        System.out.println("Learned ip " + ip + " for id " + id);
    }
}
