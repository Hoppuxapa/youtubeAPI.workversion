package com.youtube.ui.layout;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youtube.controls.search.Search;
import com.youtube.response.SimpleResponse;
import com.youtube.response.parcer.ApiResponse;
import com.youtube.response.parcer.items.*;
import com.youtube.ui.components.ImageLoader;
import com.youtube.ui.components.view.Video;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.layout.*;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.youtube.config.HttpConfig.KEY;

public class ChannelPanel {
    TextField searchText = new TextField("Enter text");
    Button searchChannel = new Button("Search");
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

    public void searchFromChannel(String searchText, ListView<GridPane> listView) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        OkHttpClient client = new OkHttpClient();
        ApiResponse searchResponse = null;
        try (Response response = client.newCall(new Request.Builder()
                .url(Objects.requireNonNull(HttpUrl.parse("https://www.googleapis.com/youtube/v3"))
                        .newBuilder()
                        .addPathSegment("search")
                        .addQueryParameter("part", "snippet")
                        .addQueryParameter("channelId", urlIDChannel)
                        .addQueryParameter("q", searchText)
                        .addQueryParameter("maxResults", "10")
                        .addQueryParameter("order", "date")
                        .addQueryParameter("key", KEY)
                        .build())
                .get()
                .build()).execute()) {
            assert response.body() != null;
            searchResponse = mapper.readValue(response.body().bytes(), new TypeReference<ApiResponse>() {
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        List<SimpleResponse> searchResults = new ArrayList<>();
        SimpleResponse result;

        assert searchResponse != null;
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

        List<GridPane> sample = new ArrayList<>();
        for (SimpleResponse searchResult : searchResults) {
            sample.add(new Video(searchResult).newList());
        }

        //make task run later in main FX thread
        ObservableList<GridPane> observableList = FXCollections.observableList(sample);
        Platform.runLater(() -> listView.setItems(observableList));
    }

    private String getFirstUrl(Thumbnails thumbnails) {
        String url;
        if (thumbnails.getRandom() != null) {
            url = thumbnails.getRandom().getUrl();
            return url;
        } else if (thumbnails.getMedium() != null) {
            url = thumbnails.getMedium().getUrl();
            return url;
        } else if (thumbnails.getStandard() != null) {
            url = thumbnails.getStandard().getUrl();
            return url;
        } else if (thumbnails.getHigh() != null) {
            url = thumbnails.getHigh().getUrl();
            return url;
        } else if (thumbnails.getMaxres() != null) {
            url = thumbnails.getMaxres().getUrl();
            return url;
        }
        return "https://i.ytimg.com/vi/yWpKll3G_a0/default.jpg";
    }

    public TextField getSearchText() {
        return searchText;
    }

    private void initUI() {
        searchText.setPrefWidth(300);
        searchText.setPromptText("enter text");
        searchChannel.setMinWidth(75);
        searchChannel.setMaxWidth(75);
        searchChannel.setText("Search");
    }

    public Pane newChannelPane() {
        VideoList channelView = new VideoList(1000, 800, 210);

        Label name = new Label(channelName);
        name.setFont(new Font("Arial", 16));
        name.setTranslateX(20);
        name.setStyle("-fx-font-weight: bold");

        TextArea description = new TextArea(channelDescription);
        description.setTranslateX(20);
        description.setMaxHeight(180);
        description.setMaxWidth(350);
        description.setEditable(false);
        VBox channelInfo = new VBox(name, description);

        searchChannel.setOnMouseClicked(event -> {
            try {
                searchFromChannel(searchText.getText(),channelView.getResultsList());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        ImageView image = new ImageView();
        image.setTranslateX(10);
        searchText.setTranslateX(30);
        searchChannel.setTranslateX(35);
        HBox header = new HBox(image, channelInfo, searchText, searchChannel);
        header.setTranslateY(5);

        //loadImage:
        new Thread(new ImageLoader(image, imageUrl)).start();

        //handle content: fill last videos
        Search controls = new Search();
        controls.channelSearch(urlIDChannel, channelView.getResultsList());

        Pane pane = new Pane();
        pane.getChildren().addAll(header, channelView.getResultsBox());
        return pane;
    }
}
