package com._98elements.mnjwtdemo;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.HttpStatus;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Controller("/elements")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class ElementsController {

    @Get("/")
    public HttpStatus index() {
        return HttpStatus.OK;
    }
}