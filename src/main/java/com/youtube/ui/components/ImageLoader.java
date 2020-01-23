package com.youtube.ui.components;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageLoader implements Runnable{
    private ImageView imageView;
    private String urlPathToImage;

    public ImageLoader(ImageView imageView, String urlPathToImage) {
        this.imageView = imageView;
        this.urlPathToImage = urlPathToImage;
    }

    @Override
    public void run() {
        Image image=new Image(urlPathToImage);
        Platform.runLater(()-> imageView.setImage(image ));
    }
}
