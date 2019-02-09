package com.hollatags.ussd.api;

import com.hollatags.ussd.AppEventListener;
import com.hollatags.ussd.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("ussd")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class UssdApi {

    @Autowired
    private AppEventListener appEventListener;


    @Path("send")
    @POST
    public Response sendUssd(Message message){
        return Response.ok(appEventListener.getConnection()
                .sendUssd(message.getSender(), message.getReceiver(),
                        message.getMessage(),message.getType()))
                .build();
    }
}
