package com.gizemcakmak.automation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PopularCouponsResponse(
        int sc,
        @JsonProperty("d") List<Coupon> coupons
) {}