/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.info;

import java.time.LocalDateTime;

/**
 *
 * @author Bennett.DenBleyker
 */
public class Post {
    
    LocalDateTime postTime;
    Object content;
    
    public Post(LocalDateTime postTime, Object content) {
        this.postTime = postTime;
        this.content = content;
    }

    public LocalDateTime getPostTime() {
        return postTime;
    }

    public Object getContent() {
        return content;
    }

    public void setPostTime(LocalDateTime postTime) {
        this.postTime = postTime;
    }

    public void setContent(Object content) {
        this.content = content;
    }
    
    
}
