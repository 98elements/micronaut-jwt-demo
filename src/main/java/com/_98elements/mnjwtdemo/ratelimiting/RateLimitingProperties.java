package com._98elements.mnjwtdemo.ratelimiting;

import io.micronaut.context.annotation.ConfigurationProperties;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Duration;

@ConfigurationProperties("rate-limiter")
class RateLimitingProperties {

    @NotNull
    Duration timeoutDuration;

    @NotNull
    Duration limitRefreshPeriod;

    @Min(1)
    @NotNull
    Integer limitForPeriod;

}
