package com.gizemcakmak.automation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Coupon(
        String couponId,
        Integer eventCount,
        List<CouponEvent> events,
        Integer orderIndex,
        String totalOdd,
        String minEventDateEpoch
) {}