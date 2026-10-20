package com.gizemcakmak.automation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Outcome(
        String id,
        String name,
        String odd,
        String status
) {}