package com.example.photoalbum;

public class AlbumData {
    private String folderName;
    private String folderLocation;
    private String thumbnailImage;

    public AlbumData(String folderName, String folderLocation, String thumbnailImage) {
        this.folderName = folderName;
        this.folderLocation = folderLocation;
        this.thumbnailImage = thumbnailImage;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderLocation() {
        return folderLocation;
    }

    public void setFolderLocation(String folderLocation) {
        this.folderLocation = folderLocation;
    }

    public String getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(String thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }
}
