package com.youtube.response.parcer.items2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Items {
    private String kind;
    private String etag;
    private String id;
    private Snippet snippet;
}
