package com.youtube.response.parcer;

import com.youtube.response.parcer.items.Items;
import com.youtube.response.parcer.items.PageInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ApiResponse {
    private String kind;
    private String etag;
    private String nextPageToken;
    private String prevPageToken;
    private String regionCode;
    private PageInfo pageInfo;
    private List<Items> items = new ArrayList<>();
}
