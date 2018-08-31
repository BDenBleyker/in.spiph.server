/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.info.packets.serializing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import in.spiph.info.packets.base.APacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 *
 * @author Bennett.DenBleyker
 */
public class PacketEncoder extends MessageToByteEncoder<APacket> {

    public static final String END_OF_PACKET_SIGNAL = "END_OF_PACKET";
    
    @Override
    protected void encode(ChannelHandlerContext ctx, APacket packet, ByteBuf out) throws Exception {
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(APacket.class, new InterfaceAdapter<APacket>());
        gb.setPrettyPrinting();
        
        Gson gson = gb.create();
        
        String json = gson.toJson(packet, APacket.class);
        for (char c : json.toCharArray()) {
            out.writeChar(c);
        }
        for (char c : PacketEncoder.END_OF_PACKET_SIGNAL.toCharArray()) {
            out.writeChar(c);
        }
    }

}
