package com.gizemcakmak.automation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CouponEvent(
        String name,
        String type,
        Integer sportId,
        BigDecimal minOdd,
        BigDecimal maxOdd,
        Long startDateEpoch,
        List<Outcome> outcomes,
        Long betradarId

        ) {}