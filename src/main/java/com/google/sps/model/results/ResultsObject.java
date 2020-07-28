package com.google.sps.model.results;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ResultsObject<T> {
    @JsonProperty
    private final List<T> results;

    @JsonProperty
    private final int total_results;

    @JsonProperty
    private final int total_pages;

    @JsonProperty
    private final int page;

    public ResultsObject(List<T> results, int totalResults, int totalPages, int page) {
        this.results = results;
        this.total_results = totalResults;
        this.total_pages = totalPages;
        this.page = page;
    }
}
