/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.info.packets.client;

import com.google.gson.internal.LinkedTreeMap;
import in.spiph.info.packets.base.APacket;
import in.spiph.info.Page;

/**
 *
 * @author Bennett.DenBleyker
 */
public class PagePacket extends APacket {

    public PagePacket(long id) {
        super(2, id + "");
    }

    public PagePacket(Page data) {
        super(2, data);
    }

    @Override
    public String toString() {
        if (getData() instanceof Page) {
            Page page = (Page) getData();
            return "PagePacket{Posts: " + page.getPosts().size() + ", Html: " + page.getHtml() + '}';
        } else if (getData() instanceof String) {
            long id = Long.valueOf((String) getData());
            return "PagePacket{Request: " + id + "}";
        } else if (getData() instanceof LinkedTreeMap) {
            setData(Page.valueOf((LinkedTreeMap) getData()));
            return toString();
        } else {
            return "PagePacket{ERROR: " + getData().getClass().toString() + getData().toString() + "}";
        }
    }
    
    @Override
    public void setData(Object data) {
        if (data instanceof LinkedTreeMap) {
            super.setData(Page.valueOf((LinkedTreeMap) data));
        } else {
            super.setData(data);
        }
    }

}
