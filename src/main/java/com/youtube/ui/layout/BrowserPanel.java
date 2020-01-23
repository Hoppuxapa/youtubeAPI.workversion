package com.youtube.ui.layout;

import com.youtube.response.parcer.ApiResponse;

import java.util.LinkedList;
import java.util.List;

public class BrowserPanel {
    private static BrowserPanel instance;

    private BrowserPanel() {
    }

    public static BrowserPanel getInstance() {
        if (instance == null) {
            instance = new BrowserPanel();
        }
        return instance;
    }

    public List<ApiResponse> history = new LinkedList<>();

    private int count = -1;

    public void addNext(ApiResponse responseYoutube) {
        history.add(responseYoutube);
        count++;
    }

    public ApiResponse getHistoryBack() {
        if ((--count) >= 0) {
            return history.get(count);
        } else {
            return history.get(++count);
        }
    }

    public ApiResponse getHistoryForward() {
        if ((++count) < history.size()) {
            return history.get(count);
        } else {
            return history.get(--count);
        }
    }

}
