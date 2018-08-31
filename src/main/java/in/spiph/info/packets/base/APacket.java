/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.info.packets.base;

import in.spiph.server.Server;
import java.io.Serializable;

/**
 *
 * @author Bennett.DenBleyker
 */
public abstract class APacket implements Serializable {
    private static long nextId = 0;
    private final long id;
    private final int type;
    private Object data;
    private String from;
    
    public APacket(int type, Object data) {
        this.id = nextId++;
        this.type = type;
        this.data = data;
        this.from = Server.name;
    }

    @Override
    public String toString() {
        return "APacket[" + id + "]{" + "from=\"" + from + "\", type=" + type + ", data=\"" + data.toString() + "\"}";
    }

    public long getId() {
        return id;
    }
    
    public int getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
    
    
    
}
