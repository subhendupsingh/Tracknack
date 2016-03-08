package com.fullsleeves.tracknack.entities;

/**
 * Created by welcome on 1/7/2016.
 */
public class Media {
    private int id;
    private String uri;
    private String title;
    private String description;
    private int isUploadCompleted;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIsUploadCompleted() {
        return isUploadCompleted;
    }

    public void setIsUploadCompleted(int isUploadCompleted) {
        this.isUploadCompleted = isUploadCompleted;
    }
}
