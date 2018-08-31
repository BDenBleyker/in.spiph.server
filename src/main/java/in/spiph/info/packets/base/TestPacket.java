/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.info.packets.base;

import in.spiph.info.packets.base.APacket;

/**
 *
 * @author Bennett.DenBleyker
 */
public class TestPacket extends APacket {
    
    //Request
    public TestPacket() {
        super(0, "Request");
    }
    
    //Response
    public TestPacket(String genericResponse) {
        super(0, genericResponse);
    }

    @Override
    public String toString() {
        return "TestPacket{" + getData() + '}';
    }
    
    
    
}
