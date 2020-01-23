package com.youtube.response.parcer.items2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Thumbnails {
    @JsonProperty("default")
    private VideoImage random;
    private VideoImage medium ;
    private VideoImage high ;
    private VideoImage standard ;
    private VideoImage maxres ;
}
