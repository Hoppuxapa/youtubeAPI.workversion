package com.youtube.ui.layout;

import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.LinkedList;

public class VideoList {
    private ListView<GridPane> resultsList = new ListView<>();
    private VBox resultsBox = new VBox(resultsList);

    public VideoList(double width, double height, double positionY) {
        resultsList.setPrefWidth(width - 50);
        resultsList.setPrefHeight(height - positionY - 50);
        resultsBox.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: cyan;");
        resultsBox.setLayoutY(positionY);
        resultsBox.setMaxSize(width, height - positionY - 50);
    }


    public VBox getResultsBox() {
        return resultsBox;
    }



    public ListView<GridPane> getResultsList() {
        return resultsList;
    }
}
