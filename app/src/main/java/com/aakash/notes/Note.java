package com.aakash.notes;

import java.io.Serializable;

/**
 * Created by akash on 15-Oct-17.
 */

class Note implements Serializable{

    private String title;
    private String content;
    private String username;

    Note(){}

    Note(String title, String content, String username){
        this.title = title;
        this.content = content;
        this.username = username;
    }

    String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
