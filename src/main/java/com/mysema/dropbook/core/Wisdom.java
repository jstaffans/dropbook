package com.mysema.dropbook.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.json.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.joda.time.DateTime;

import java.util.Date;

@Data
@AllArgsConstructor
@JsonSnakeCase
public class Wisdom {

    @JsonProperty
    final int id;

    @JsonProperty
    final String quote;

    @JsonProperty
    final DateTime addedOn;

    @JsonIgnore
    public String getSqlDatetime() {
        return addedOn.toString();
    }
}


