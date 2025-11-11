package org.example.productservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.autoconfigure.server.GrpcServerProperties;
import org.springframework.util.unit.DataSize;

@Configuration
public class GrpcServerConfig {

    @Bean
    public GrpcServerProperties grpcServerProperties() {
        GrpcServerProperties properties = new GrpcServerProperties();
        properties.setPort(9090);
        properties.setMaxInboundMessageSize(DataSize.ofMegabytes(4));
        return properties;
    }
}
