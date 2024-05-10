package com.infilos.server.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Http2HttpsConfigure {

    /**
     * http 的端口
     */
    @Value("${server.http-port}")
    private int httpPort;

    /**
     * https 的端口
     */
    @Value("${server.port}")
    private int httpsPort;

//    /**
//     * HTTP 请求将被转为 HTTPS 请求
//     */
//    @Bean
//    public TomcatServletWebServerFactory servletContainerFactory() {
//        TomcatServletWebServerFactory tomcatFactory = new TomcatServletWebServerFactory() {
//            @Override
//            protected void postProcessContext(Context context) {
//                // 设置安全性约束
//                SecurityConstraint securityConstraint = new SecurityConstraint();
//                securityConstraint.setUserConstraint("CONFIDENTIAL");
//
//                // 设置约束条件,拦截所有请求
//                SecurityCollection securityCollection = new SecurityCollection();
//                securityCollection.addPattern("/*");
//
//                securityConstraint.addCollection(securityCollection);
//
//                context.addConstraint(securityConstraint);
//            }
//        };
//
//        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//        // 设置将分配给通过此连接器接收到的请求的方案
//        connector.setScheme("http");
//        // true： http使用http, https使用https;
//        // false： http重定向到https;
//        connector.setSecure(false);
//        // 设置监听请求的端口号，这个端口不能其他已经在使用的端口重复，否则会报错
//        connector.setPort(httpPort);
//        // 重定向端口号(非SSL到SSL)
//        connector.setRedirectPort(httpsPort);
//        tomcatFactory.addAdditionalTomcatConnectors(connector);
//
//        return tomcatFactory;
//    }

    /**
     * 启用 HTTP 端口，同时支持 HTTPS 和 HTTP 请求
     */
    @Bean
    public ServletWebServerFactory serverFactory() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(httpPort);

        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(connector);

        return tomcat;
    }
}
