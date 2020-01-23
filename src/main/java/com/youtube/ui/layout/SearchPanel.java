package com.youtube.ui.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SearchPanel {
    private static boolean clickCount = false;



    public Button getLeftButton() {
        return leftButton;
    }

    public Button getRightButton() {
        return rightButton;
    }

    private Button leftButton = new Button("Back");

    private Button rightButton = new Button("Forward");


    //search set
    private Button search = new Button("Search");
    private Button advanced = new Button("Advanced");
    private TextField searchText = new TextField();
    private HBox searchBox = new HBox(searchText, search, advanced, leftButton, rightButton);


    //advanced components
    private TextField maxRes = new TextField();
    private TextField daysPublished = new TextField();
    private Button searchAdvanced = new Button("Search");
    private HBox searchBoxExtend = new HBox(maxRes, daysPublished, searchAdvanced);

    private VBox result;

    public SearchPanel() {
        initUI();
    }

    private void initUI() {
        //* setting up search button properties
        search.setMinWidth(75);
        search.setMaxWidth(75);
        search.setText("Search");

        //history buttons
        leftButton.setMinWidth(70);
        leftButton.setMaxWidth(70);
        rightButton.setMaxWidth(70);
        rightButton.setMinWidth(70);

        //* setting up advanced button properties
        advanced.setMinWidth(100);
        advanced.setMaxWidth(100);
        advanced.setText("Advanced");

        searchText.setPrefWidth(300);
        searchText.setPromptText("enter search text");

        searchBox.setSpacing(15);
        searchBox.setPadding(new Insets(10, 5, 5, 5));

        searchAdvanced.setMinWidth(75);
        searchAdvanced.setMaxWidth(75);

        maxRes.setPrefWidth(142);
        maxRes.setPromptText("max results");
        daysPublished.setPrefWidth(142);
        daysPublished.setPromptText("days published");

        searchBoxExtend.setSpacing(15);
        searchBoxExtend.setPadding(new Insets(5, 5, 5, 5));

        result = new VBox();
        result.setAlignment(Pos.CENTER);
        result.setSpacing(10);
        result.setPadding(new Insets(10, 10, 10, 10));
        result.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: blue;");
        result.getChildren().addAll(searchBox, searchBoxExtend);

        searchBoxExtend.setVisible(clickCount);
        advanced.setOnMouseClicked(event -> {
            clickCount = !clickCount;
            if (clickCount) {
                searchBoxExtend.setVisible(true);
                System.out.println("show advanced fields");
            } else {
                searchBoxExtend.setVisible(false);
                System.out.println("hide advanced fields");
            }
        });
    }

    public Button getSearch() {
        return search;
    }

    public TextField getSearchText() {
        return searchText;
    }

    public TextField getMaxRes() {
        return maxRes;
    }

    public TextField getDaysPublished() {
        return daysPublished;
    }

    public Button getSearchAdvanced() {
        return searchAdvanced;
    }

    public VBox searchPanel() {
        return result;
    }
}
