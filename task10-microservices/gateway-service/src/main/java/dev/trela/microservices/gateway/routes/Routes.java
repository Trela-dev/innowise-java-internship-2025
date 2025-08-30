package dev.trela.microservices.gateway.routes;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.*;

import static org.springframework.web.servlet.function.RouterFunctions.route;


@Configuration
public class Routes {

@Bean
public RouterFunction<ServerResponse> fallbackRoute() {
    return route()
            .route(request -> request.path().equals("/fallbackRoute"), request ->
                    ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                            .body("Service unavailable, please try again later."))
            .build();
}

}
