package com.yxkang.android.sample.bean;

import android.graphics.drawable.Drawable;

public class FileInfoBean {

    private long id;
    private String absolutePath;
    private String displayName;
    private long fileSize;
    private String fileTitle;
    private String mimeType;
    private long dateAdd;
    private long dateModified;
    private Drawable fileIcon;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileTitle() {
        return fileTitle;
    }

    public void setFileTitle(String fileTitle) {
        this.fileTitle = fileTitle;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(long dateAdd) {
        this.dateAdd = dateAdd;
    }

    public long getDateModified() {
        return dateModified;
    }

    public void setDateModified(long dateModified) {
        this.dateModified = dateModified;
    }

    public Drawable getFileIcon() {
        return fileIcon;
    }

    public void setFileIcon(Drawable fileIcon) {
        this.fileIcon = fileIcon;
    }

}
