package com.youtube.ui.layout;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youtube.controls.search.Search;
import com.youtube.response.SimpleResponse;
import com.youtube.response.parcer.ApiResponse;
import com.youtube.response.parcer.items.Items;
import com.youtube.ui.components.ImageLoader;
import com.youtube.ui.components.view.Channel;
import com.youtube.ui.components.view.Video;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.youtube.config.HttpConfig.KEY;

public class ChannelPanel {

    // Этот метод дает запрос на поиск по каналу
    public void searchFromChannel() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        OkHttpClient client = new OkHttpClient();
        ApiResponse searchResponse = null;
        try (Response response = client.newCall(new Request.Builder()
                .url(HttpUrl.parse("https://www.googleapis.com/youtube/v3")
                        .newBuilder()
                        .addPathSegment("search")
                        .addQueryParameter("part", "snippet")
                        .addQueryParameter("channelId", urlIDChannel)
                        .addQueryParameter("q", "best")
                        .addQueryParameter("maxResults", "10")
                        .addQueryParameter("order", "date")
                        .addQueryParameter("key", KEY)
                        .build())
                .get()
                .build()).execute()) {
            searchResponse = mapper.readValue(response.body().bytes(), new TypeReference<ApiResponse>() {
            });

        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<SimpleResponse> searchResults = new ArrayList<>();
        SimpleResponse result;

        for (Items item : searchResponse.getItems()) {
            if (item.getId().getVideoId() != null) {
                result = new SimpleResponse.Builder()
                        .setVideoName(item.getSnippet().getTitle())
                        .setChannelName(item.getSnippet().getChannelTitle())
                        .setPublicationDate(item.getSnippet().getPublishedAt())
                        .setUrlID(item.getId().getVideoId())
                        .setUrlIDChannel(item.getSnippet().getChannelId())
                        .setUrlPathToImage(getFirstUrl(item.getSnippet().getThumbnails()))
                        .build();
                searchResults.add(result);

            }
        }
        // return either objects for general search or channel search
        // current solution
        //todo: make one adaptive class
        List<GridPane> sample = new ArrayList<>();
        if (generalSearch) {
            for (SimpleResponse searchResult : searchResults) {
                sample.add(new Video(searchResult).newList());
            }
        } else {
            for (SimpleResponse searchResult : searchResults) {
                sample.add(new Channel(searchResult).newList());
            }
        }

        //make task run later in main FX thread save from - "IllegalStateException: Not on FX application thread"
        ObservableList<GridPane> observableList = FXCollections.observableList(sample);
        Platform.runLater(() -> listView.setItems(observableList));
    }


    TextField searchText = new TextField("Enter text");
    Button searchChannel = new Button("Search");

    private void initUI() {
        searchText.setPrefWidth(300);
        searchText.setPromptText("enter text");
        searchChannel.setMinWidth(75);
        searchChannel.setMaxWidth(75);
        searchChannel.setText("Search");
    }

    private String channelName;
    private String channelDescription;
    private String urlIDChannel;
    private String imageUrl;

    public ChannelPanel(String channelName, String channelDescription, String urlIDChannel, String imageUrl) {
        System.out.println("New channel created!! " + Thread.currentThread().getName()
                + " channelID: " + urlIDChannel);
        this.channelName = channelName;
        this.channelDescription = channelDescription;
        this.urlIDChannel = urlIDChannel;
        this.imageUrl = imageUrl;
    }

    //todo: do nice layout - userUI
    public Pane newChannelPane() {

        VideoList channelView = new VideoList(700, 850, 210);

        Label name = new Label(channelName);
        Label description = new Label(channelDescription);
        VBox channelInfo = new VBox(name, description);


        searchChannel.setOnMouseClicked(event -> {
            searchFromChannel();
        });

//        ImageView channelImage = new ImageView(new Image("https://i.ytimg.com/vi/yWpKll3G_a0/default.jpg"));
        ImageView image = new ImageView();
        HBox header = new HBox(image, channelInfo, searchText, searchChannel);

        //loadImage:
        System.out.println("URL for images - " + imageUrl + " | " + this.getClass().getSimpleName());
        new Thread(new ImageLoader(image, imageUrl)).start();

        //handle content: fill last videos
        Search controls = new Search();
        controls.channelSearch(urlIDChannel, channelView.getResultsList());

        Pane pane = new Pane();
        pane.getChildren().addAll(header, channelView.getResultsBox());
        return pane;


    }
}
