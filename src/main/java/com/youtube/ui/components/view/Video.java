package com.youtube.ui.components.view;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youtube.config.Config;
import com.youtube.config.HttpConfig;
import com.youtube.controls.builder.HttpBuilder;
import com.youtube.response.SimpleResponse;
import com.youtube.response.parcer.items2.NewApiResponse;
import com.youtube.ui.components.ImageLoader;
import com.youtube.ui.layout.ChannelPanel;
import com.youtube.ui.popup.AlertBox;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

import javax.swing.text.WrappedPlainView;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Video extends ListCell<String> implements Config, HttpConfig {
    private Button view;
    private GridPane gridPane;
    private Label videoName;
    private Label channelName;
    private ImageView imageView;

    public Video(SimpleResponse searchResult) throws IOException {
        view = new Button("View");
        videoName = new Label(searchResult.getVideoName());
        channelName = new Label(searchResult.getChannelName());
        channelName.setStyle("-fx-font-weight: bold;");
        Label published = new Label(LocalDateTime.parse(searchResult.getPublicationDate(),
                DateTimeFormatter.ofPattern(DATE_FORMAT)).format(DateTimeFormatter.ofPattern(DATE_FORMAT_SHOW)));
        imageView = new ImageView();
        gridPane = new GridPane();
        ObjectMapper mapper = new ObjectMapper();

        //todo: for channel description we need to do search in CHANNEL and put it here!!!

        String desc = null;
        try(Response response = client.newCall(new Request.Builder()
                .url(HttpUrl.parse("https://www.googleapis.com/youtube/v3")
                        .newBuilder()
                        .addPathSegment("channels")
                        .addQueryParameter("part", "snippet")
                        //.addQueryParameter("maxResults", maxResults)
                        .addQueryParameter("id", searchResult.getUrlIDChannel())
                        .addQueryParameter("key", KEY)
                        .build())
                .get()
                .build()).execute()){
            //String resp = response.body().string();
            NewApiResponse apiResponse = mapper.readValue(response.body().bytes(), new TypeReference<NewApiResponse>(){});
            //System.out.println(resp);
                desc = apiResponse.getItems().get(0).getSnippet().getDescription();
        }

        channelNameActions(searchResult.getChannelName(), desc, searchResult.getUrlIDChannel(), searchResult.getUrlPathToImage());

        onClick(HttpBuilder.buildYouTubeWatchUrl(searchResult.getUrlID()));

        imageView.setFitHeight(30);
        imageView.setFitWidth(45);
        gridPane.add(imageView, 0, 0);
        gridPane.add(view, 1, 0);
        gridPane.add(videoName, 2, 0);
        gridPane.add(channelName, 3, 0);
        gridPane.add(published, 4, 0);
        gridPane.setHgap(10);
        gridPane.setVgap(8);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        loadImage(searchResult.getUrlPathToImage());
    }

    private void channelNameActions(String channelName, String description, String urlIDChannel, String urlImage) {
        this.channelName.setCursor(Cursor.HAND);
        this.channelName.setOnMouseClicked(event -> {
            if(urlIDChannel != null) {
                Stage stage = new Stage();
                stage.setTitle(String.valueOf(channelName));
                stage.initModality(Modality.APPLICATION_MODAL);
                ChannelPanel channel = new ChannelPanel(channelName, description, urlIDChannel, urlImage);
                Scene scene = new Scene(channel.newChannelPane());
                stage.setScene(scene);
                stage.setOnCloseRequest(event1 -> System.out.println("Channel watch terminated!"));
                stage.show();
            }else {
                AlertBox.display("No Channel", "No Channel found!");
            }
        });
    }

    private void loadImage(String urlPathToImage) {
        System.out.println("URL for images - " + urlPathToImage + " | " + this.getClass().getSimpleName());
        new Thread(new ImageLoader(imageView,urlPathToImage)).start();
    }

    private void onClick(String videoURL) {
        view.setOnMouseClicked(event -> {
            WebView webView = new WebView();
            System.out.println("videoURL" + videoURL + "  " + this.getClass());
            webView.getEngine().load(videoURL);
            webView.setPrefSize(640, 390);

            Stage stage = new Stage();
            stage.setTitle("Watch video form");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(webView));
            stage.setOnCloseRequest(event1 -> {
                webView.getEngine().load("");
                System.out.println("video stream was closed");
            });
            stage.show();
        });
    }

    public GridPane newList() {
        gridPane.setHgap(15);
        return gridPane;
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setEditable(false);
        if (videoName != null || channelName != null) {
            setGraphic(gridPane);
        } else {
            setGraphic(null);
        }
    }
}
