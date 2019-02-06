package com._98elements.mnjwtdemo

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.security.authentication.UsernamePasswordCredentials
import io.micronaut.security.token.jwt.render.AccessRefreshToken
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

class ElementsControllerSpec extends Specification {

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer)

    @Shared
    @AutoCleanup
    RxHttpClient client = embeddedServer.applicationContext.createBean(RxHttpClient, embeddedServer.getURL())

    @Shared
    UserRepository userRepository = embeddedServer.applicationContext.getBean(UserRepository)

    String authorization

    void setup() {
        def user = userRepository.create("john.smith@98elements.com", UUID.randomUUID().toString())

        def request = new UsernamePasswordCredentials(user.username, user.password)
        def tokenPair = client.toBlocking().retrieve(HttpRequest.POST("/login", request), AccessRefreshToken)

        authorization = "Bearer ${tokenPair.accessToken}"
    }

    void "test index"() {
        given:
        def request = HttpRequest.GET("/elements").header(HttpHeaders.AUTHORIZATION, authorization)

        when:
        HttpResponse response = client.toBlocking().exchange(request)

        then:
        response.status == HttpStatus.OK
    }
}
