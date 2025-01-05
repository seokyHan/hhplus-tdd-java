package io.hhplus.tdd.point.controller.request;

import jakarta.validation.constraints.Positive;

public record UserPointRequest(
        @Positive
        long amount
) {
}
