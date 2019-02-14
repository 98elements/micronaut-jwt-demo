package com._98elements.mnjwtdemo.ratelimiting;

import com._98elements.mnjwtdemo.ErrorResponse;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.internal.AtomicRateLimiter;
import io.github.resilience4j.ratelimiter.internal.AtomicRateLimiter.AtomicRateLimiterMetrics;
import io.micronaut.cache.SyncCache;
import io.micronaut.core.order.Ordered;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.FilterOrderProvider;
import io.micronaut.http.filter.OncePerRequestHttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import javax.inject.Named;
import java.security.Principal;
import java.time.Duration;

@Filter("/**")
public class RateLimitingFilter extends OncePerRequestHttpServerFilter implements FilterOrderProvider {

    private final SyncCache<AtomicRateLimiter> limiters;

    private final RateLimiterConfig config;

    public RateLimitingFilter(@Named("rate-limiter") SyncCache<AtomicRateLimiter> limiters,
                              RateLimitingProperties properties) {
        this.limiters = limiters;
        this.config = RateLimiterConfig.custom()
                .limitRefreshPeriod(properties.limitRefreshPeriod)
                .limitForPeriod(properties.limitForPeriod)
                .timeoutDuration(properties.timeoutDuration)
                .build();
    }

    @Override
    protected Publisher<MutableHttpResponse<?>> doFilterOnce(HttpRequest<?> request, ServerFilterChain chain) {
        var key = getKey(request);
        var limiter = getLimiter(key);

        if (limiter.getPermission(config.getTimeoutDuration())) {
            return chain.proceed(request);
        } else {
            return createOverLimitResponse(limiter.getDetailedMetrics());
        }
    }

    private String getKey(HttpRequest<?> request) {
        return request.getUserPrincipal()
                .map(Principal::getName)
                .orElseGet(() -> request.getRemoteAddress().getAddress().getHostAddress());
    }

    private AtomicRateLimiter getLimiter(String key) {
        return limiters.get(key, AtomicRateLimiter.class, () ->
                new AtomicRateLimiter(key, config)
        );
    }

    private Publisher<MutableHttpResponse<?>> createOverLimitResponse(AtomicRateLimiterMetrics metrics) {
        var secondsToWait = Duration.ofNanos(metrics.getNanosToWait()).toSeconds();

        var message = "Maximum request rate exceeded. Wait " + secondsToWait + "s before issuing a new request";

        var body = new ErrorResponse(message);

        return Flowable.just(
                HttpResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                        .header(HttpHeaders.RETRY_AFTER, String.valueOf(secondsToWait))
                        .body(body)
        );
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
