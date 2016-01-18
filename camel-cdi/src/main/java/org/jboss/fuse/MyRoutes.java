package org.jboss.fuse;

import javax.inject.Inject;

import org.apache.camel.Endpoint;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.cdi.Uri;

/**
 * Configures all our Camel routes, components, endpoints and beans
 */
@ContextName("myCdiCamelContext")
public class MyRoutes extends RouteBuilder {

    @Inject
    @Uri("timer:foo?period=5000")
    private Endpoint inputEndpoint;

    @Inject
    //@Uri("netty4-http:http://localhost:8080/myweb/camel/hello?keepalive=false&disconnect=true")
    @Uri("netty4-http:http://172.17.0.4:8080/camel/hello?keepalive=false&disconnect=true")
    private Endpoint httpEndpoint;

    @Inject
    @Uri("log:output")
    private Endpoint resultEndpoint;

    @Inject
    private SomeBean someBean;

    @Override
    public void configure() throws Exception {
        // you can configure the route rule with Java DSL here

        from(inputEndpoint)
                .setHeader("name",method(someBean))
            .to(httpEndpoint)
            .to(resultEndpoint);
    }

}
