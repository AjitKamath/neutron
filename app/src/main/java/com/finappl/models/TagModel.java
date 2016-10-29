package com.finappl.models;

/**
 * Created by ajit on 31/1/15.
 */
public class TagModel {

    private String tag;
    private String tagId;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public TagModel(String tag, String tagId) {
        this.tag = tag;
        this.tagId = tagId;
    }

    public TagModel() {}

    public TagModel(String tag) {
        this.tag = tag;
    }
}
