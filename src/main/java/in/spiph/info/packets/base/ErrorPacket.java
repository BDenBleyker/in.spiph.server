/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.info.packets.base;

/**
 *
 * @author Bennett.DenBleyker
 */
public class ErrorPacket extends APacket {

    public ErrorPacket(String reason) {
        super(-1, "\033[31m" + reason + "\033[0m");
    }

    @Override
    public String toString() {
        return "ErrorPacket{" + getData() + '}';
    }
    
}
