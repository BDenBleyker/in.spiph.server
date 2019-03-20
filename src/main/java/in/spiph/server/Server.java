/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.server;

import in.spiph.info.ErrorPage;
import in.spiph.info.HomePage;
import in.spiph.info.Page;
import in.spiph.info.Post;
import in.spiph.info.ProfilePage;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bennett.DenBleyker
 */
public class Server {

    public static String name = "S" + Math.random() + "S";
    public static Page page;
    public static List<String> trackerIps = new ArrayList();
    private static final Map<String, String> IPLIST = new HashMap();
    private static final Page HOME_PAGE = new HomePage("");

    public static void main(String[] args) throws Exception {
        Logger.getLogger(Server.class.getName()).setLevel(Level.WARNING);

        System.setProperty("spiphi.port", "4198");

        trackerIps.add("127.27.20.30");

        List<Post> posts = new ArrayList();
        posts.add(new Post(LocalDateTime.now(), "The snow has fallen"));
        posts.add(new Post(LocalDateTime.now().minusYears(2), "What is this?!?!?"));
        posts.add(new Post(LocalDateTime.now().minusDays(2), "What is this?!?!?"));
        posts.add(new Post(LocalDateTime.now().minusMinutes(5), "What is this?!?!?"));
        posts.add(new Post(LocalDateTime.now().minusMinutes(2), "What is this?!?!?"));
        page = new ProfilePage(posts, "Skylzaar", "Strubiiiin", "", "", 100, "Words");

        new Thread(new PacketServer()).start();
    }

    public static void handleClientPacket(ChannelPipeline pipeline, APacket packet) {
        switch (packet.getType()) {
            case TestPacket.TYPE_VALUE:
                switch (packet.getData().toString()) {
                    case "Request":
                        pipeline.fireUserEventTriggered(new TestPacket("Hello!"));
                        break;
                    default:
                        Logger.getLogger(Server.class.getName()).log(Level.INFO, "Test Succeeds");
                }
                break;
            case PagePacket.TYPE_VALUE:
                System.out.println(packet.getData().toString());
                if (packet.getData() instanceof Page) {
                    page = (Page) packet.getData();
                } else if (packet.isRequest()) {
                    if (IPLIST.containsKey(packet.getData().toString())) {
                        if (!IPLIST.get(packet.getData().toString()).equals("X")) {
                            PacketServerClient server = new PacketServerClient(pipeline);
                            server.handler.fireUserEventTriggered(packet);
                            try {
                                server.initialize(IPLIST.get(packet.getData().toString()));
                            } catch (IOException | InterruptedException ex) {
                                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Server closed", ex);
                            }
                        } else {
                            pipeline.fireUserEventTriggered(new PagePacket(new ErrorPage(404, "This ID does not have a corresponding IP")));
                            IPLIST.remove(packet.getData().toString());
                        }
                    } else if (packet.getData().toString().equalsIgnoreCase("home")) {
                        pipeline.fireUserEventTriggered(new PagePacket(Server.HOME_PAGE));
                    } else if (packet.getData().toString().startsWith("error ") && packet.getData().toString().split(" ").length > 1) {
                        String[] packetSplit = packet.getData().toString().split(" ");
                        pipeline.fireUserEventTriggered(new PagePacket(new ErrorPage(Integer.valueOf(packetSplit[1]), packet.getData().toString().replace("error " + packetSplit[1] + " ", ""))));
                    } else if (packet.getData().toString().equals("self")) {
                        pipeline.fireUserEventTriggered(new PagePacket(page));
                    } else {
                        PacketServerClient tracker = new PacketServerClient(); //Cannot use the same port because they are on the same machine
                        tracker.handler.fireUserEventTriggered(new IpPacket((String) packet.getData()));
                        try {
                            tracker.initialize(trackerIps.get(0));
                        } catch (IOException | InterruptedException ex) {
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Tracker closed", ex);
                        }
                        pipeline.fireUserEventTriggered(packet);
                    }
                }
                break;
            default: // ErrorPacket
                Logger.getLogger(Server.class.getName()).log(Level.WARNING, "Invalid packet id ({0}): {1}", new Object[]{packet.getType(), packet.toString()});
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
                        Logger.getLogger(Server.class.getName()).log(Level.INFO, "Test Succeeds");
                }
                break;
            case 2: // PagePacket
                if (packet.getData() instanceof String) {
                    pipeline.fireUserEventTriggered(new PagePacket(page));
                }
                break;
            default: // ErrorPacket
                Logger.getLogger(Server.class.getName()).log(Level.WARNING, "Invalid packet id ({0}): {1}", new Object[]{packet.getType(), packet.toString()});
        }
    }

    public static void addIp(String id, String ip) {
        IPLIST.put(id, ip);
        Logger.getLogger(Server.class.getName()).log(Level.INFO, "Learned ip {0} for id {1}", new Object[]{ip, id});
    }
}
