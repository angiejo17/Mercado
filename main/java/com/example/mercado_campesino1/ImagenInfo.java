package com.example.mercado_campesino1;

public class ImagenInfo {
    private String imageName;
    private String title;
    private String description;
    private String imageUrl;

    // Constructor vacío requerido por Firebase Realtime Database
    public ImagenInfo() {
    }

    public ImagenInfo(String imageName, String title, String description, String imageUrl) {
        this.imageName = imageName;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
