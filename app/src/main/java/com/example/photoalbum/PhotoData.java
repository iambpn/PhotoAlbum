package com.example.photoalbum;

public class PhotoData {
    private String photoLocation;
    private String photoName;

    public PhotoData(String photoLocation, String photoName) {
        this.photoLocation = photoLocation;
        this.photoName = photoName;
    }

    public String getPhotoLocation() {
        return photoLocation;
    }

    public void setPhotoLocation(String photoLocation) {
        this.photoLocation = photoLocation;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }
}
