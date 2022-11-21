package org.apache.camel.example;

import org.apache.camel.builder.RouteBuilder;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.timer;

public class MyRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from(timer("tracer").period(1000L)).routeId("tracer")
                .log("the time is in this camel route is ${date:now:yyyyMMddHHmmssSSS}");


    }
}
