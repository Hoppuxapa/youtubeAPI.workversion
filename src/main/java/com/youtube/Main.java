package com.youtube;

import com.youtube.controls.Controller;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    // version 0.0.3

    @Override
    public void start(Stage primaryStage) throws Exception {
        Controller controller = new Controller();
        controller.run(primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
