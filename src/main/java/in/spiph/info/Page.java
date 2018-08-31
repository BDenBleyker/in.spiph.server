/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.info;

import com.google.gson.internal.LinkedTreeMap;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bennett.DenBleyker
 */
public class Page {

    List<Post> posts;
    String html;

    public Page(List<Post> posts, String html) {
        this.posts = posts;
        this.html = html;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
    
    public static Page valueOf(LinkedTreeMap ltm) {
        List<Post> posts = new ArrayList();
        posts.addAll((ArrayList<Post>) ltm.get("posts"));
        return new Page(posts, ltm.get("html").toString());
    }

}
