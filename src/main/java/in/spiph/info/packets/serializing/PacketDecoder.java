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
import io.netty.handler.codec.ByteToMessageDecoder;
import java.nio.charset.Charset;
import java.util.List;

/**
 *
 * @author Bennett.DenBleyker
 */
public class PacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!in.toString(Charset.defaultCharset()).replaceAll("\\u0000", "").contains(PacketEncoder.END_OF_PACKET_SIGNAL)) {
            System.out.println("Awaiting more info");
        } else {
            String singlePacket = "";
            while (!singlePacket.endsWith(PacketEncoder.END_OF_PACKET_SIGNAL)) {
                char c = in.readChar();
                if (c != '\u0000') {
                    singlePacket += c;
                }
            }
            singlePacket = singlePacket.replaceAll(PacketEncoder.END_OF_PACKET_SIGNAL, "");
            GsonBuilder gb = new GsonBuilder();
            gb.registerTypeAdapter(APacket.class, new InterfaceAdapter<APacket>());
            gb.setPrettyPrinting();

            Gson gson = gb.create();

            APacket packet = gson.<APacket>fromJson(singlePacket, APacket.class);
            out.add(packet);
        }
    }

}
