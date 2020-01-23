package com.youtube.response.parcer.items;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Items {
    private String kind;
    private String etag;
    private SearchID id;
    private Snippet snippet;
}
