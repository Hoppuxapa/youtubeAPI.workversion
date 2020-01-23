package com.youtube.controls;

import com.youtube.config.Config;
import com.youtube.controls.search.Search;
import com.youtube.ui.layout.BrowserPanel;
import com.youtube.ui.layout.SearchPanel;
import com.youtube.ui.layout.VideoList;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;


public class Controller implements Config {
    private Search controls = new Search();


    public void run(Stage window) {
        setupStage(window);
        Scene mainScene;

        SearchPanel searchLayout = new SearchPanel();
        VideoList searchView = new VideoList(WIDTH, HEIGHT, 110);
        VBox layout1 = new VBox();
        layout1.getChildren().addAll(searchLayout.searchPanel(), searchView.getResultsBox());

        Pane pane1 = new Pane();
        pane1.getChildren().add(layout1);
        mainScene = new Scene(pane1);

        // configure iu: all things on main screen
        window.setScene(mainScene);


        // buttons functionality
        searchLayout.getSearch().setOnMouseClicked(e -> controls.search(searchLayout.getSearchText().getText(),
                searchView.getResultsList()));


        searchLayout.getLeftButton().setOnMouseClicked(e -> {
            try {
                controls.showHistory(searchView.getResultsList(),BrowserPanel.getInstance().getHistoryBack());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        searchLayout.getRightButton().setOnMouseClicked(e -> {
            try {
                controls.showHistory(searchView.getResultsList(),BrowserPanel.getInstance().getHistoryForward());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        searchLayout.getSearchAdvanced().setOnMouseClicked(e -> controls.search(searchLayout.getSearchText().getText(),
                searchLayout.getMaxRes().getText(),
                searchLayout.getDaysPublished().getText(),
                searchView.getResultsList()));

        // closing okhttp client on program close
        window.setOnCloseRequest(event -> {
            client.dispatcher().executorService().shutdown();
            System.out.println("client OkHTTP was closed");
        });
    }

    private void setupStage(Stage stage) {
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);

        stage.setMinWidth(WIDTH);
        stage.setMinHeight(HEIGHT);

        stage.setMaxWidth(Double.MAX_VALUE);
        stage.setMaxHeight(Double.MAX_VALUE);
        stage.setTitle("YouTube Search");
    }
}
