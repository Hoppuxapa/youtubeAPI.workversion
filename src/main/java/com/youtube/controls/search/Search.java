package com.youtube.controls.search;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youtube.config.Config;
import com.youtube.controls.builder.HttpBuilder;
import com.youtube.response.SimpleResponse;
import com.youtube.response.parcer.ApiResponse;
import com.youtube.response.parcer.items.*;
import com.youtube.ui.ConsoleColors;
import com.youtube.ui.components.view.*;
import com.youtube.ui.layout.BrowserPanel;
import com.youtube.ui.popup.AlertBox;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import okhttp3.*;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Search implements Config {
    private static HttpUrl http;
    private static ObjectMapper mapper = new ObjectMapper();

    public void showHistory(ListView<GridPane> listView, ApiResponse responseYoutube) throws IOException {
        List<SimpleResponse> searchResults = new ArrayList<>();

        SimpleResponse result;
        for (Items item : responseYoutube.getItems()) {
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
//            System.out.println("History: " + searchResult.toString());
            sample.add(new Video(searchResult).newList());
        }

        //make task run later in main FX thread
        ObservableList<GridPane> observableList = FXCollections.observableList(sample);
        Platform.runLater(() -> listView.setItems(observableList));
    }

    public void channelSearch(String channelID, ListView<GridPane> listView) {
        http = HttpBuilder.buildChannelHttpUrl(channelID);
        searchEngine(http, listView, false);
    }

    public void search(String searchText, ListView<GridPane> listView) {
        if (searchText.isEmpty()) {
            AlertBox.display("Search text", "There is no text in search field!");
            return;
        }
        System.out.println(ConsoleColors.BLUE_BOLD + "Search request: " + searchText + ConsoleColors.RESET);
        http = HttpBuilder.buildHttpUrl(searchText);
        searchEngine(http, listView, true);
    }

    public void search(String searchText, String maxRes, String daysPublished, ListView<GridPane> resultsList) {
        if (searchText.isEmpty()) {
            AlertBox.display("Search text", "There is no text in search field!");
            return;
        }

        if (notPositiveInteger(maxRes)) {
            AlertBox.display("Max Results", "Max results not indicated!");
            return;
        }

        if (notPositiveInteger(daysPublished)) {
            AlertBox.display("Days published", "Days published not indicated!");
            return;
        }

        //reformat date for search request to 1970-01-01T00:00:00Z
        LocalDateTime newDate = LocalDateTime.now().minusDays(Integer.parseInt(daysPublished));
        String publishedAfter = newDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT));

        http = HttpBuilder.buildHttpUrl(searchText, maxRes, publishedAfter);
        searchEngine(http, resultsList, true);
    }

    private boolean notPositiveInteger(String text) {
        if (text.isEmpty()) {
            return true;
        }
        for (int i = 0; i < text.length(); i++) {
            if (i == 0 && text.charAt(i) == '-') {
                if (text.length() == 1) {
                    return true;
                }
            }
            if (Character.digit(text.charAt(i), 10) < 0) {
                return true;
            }
        }
        return false;
    }

    private void searchEngine(HttpUrl http, ListView<GridPane> listView, boolean generalSearch) {
        Call call = client.newCall(new Request.Builder()
                .url(http)
                .get()
                .build());
        // asynchronous call
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // response from YouTube
                    assert response.body() != null : "response.body() = null";
                    ApiResponse responseYoutube = mapper.readValue(response.body().bytes(), new TypeReference<ApiResponse>() {
                    });

                    BrowserPanel.getInstance().addNext(responseYoutube);

                    System.out.println("Response code: " + ConsoleColors.RED_BOLD_BRIGHT + response.code() + ConsoleColors.RESET + "\n");

                    List<SimpleResponse> searchResults = new ArrayList<>();
                    SimpleResponse result;

                    for (Items item : responseYoutube.getItems()) {
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
            }

            @Override
            public void onFailure(Call call, IOException e) {
                try {
                    if (call.execute().code() >= 400 && call.execute().code() < 500)
                        System.out.println("Error on client side!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                System.out.println("Error:");
                System.out.println(Arrays.toString(e.getStackTrace()));
            }
        });
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
}
