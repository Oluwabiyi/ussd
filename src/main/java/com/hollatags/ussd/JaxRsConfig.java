package com.hollatags.ussd;

import com.hollatags.ussd.api.UssdApi;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

@Component
@ApplicationPath("api/v1.0")
public class JaxRsConfig extends ResourceConfig {

    public JaxRsConfig(){
        this.registerApi();
    }

    private void registerApi(){
        register(UssdApi.class);
    }
}
