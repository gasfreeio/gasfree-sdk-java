package org.tron.gasfree.sdk;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.tron.walletserver.AddressUtil;

public class ParamCheck {
    public static boolean isValidParma(String eip712Message) throws Exception {
        Gson gson = new Gson();
        JsonObject data
                = gson.fromJson(eip712Message, JsonObject.class);
        String message = data.get("message").toString();
        GasFreeMessage object = gson.fromJson(message, GasFreeMessage.class);
        if(object == null){
            throw new Exception("Invalid input message");
        }
        if(!AddressUtil.isAddressValid(object.getToken())){
            throw new AddressException("Invalid message.token: ${"+object.getToken() + "}, should be a valid Tron address");
        }
        if(!AddressUtil.isAddressValid(object.getUser())){
            throw new AddressException("Invalid message.user: ${"+object.getUser() + "}, should be a valid Tron address");
        }
        if(!AddressUtil.isAddressValid(object.getReceiver())){
            throw new AddressException("Invalid message.receiver: ${"+object.getReceiver() + "}, should be a valid Tron address");
        }
        if(!AddressUtil.isAddressValid(object.getServiceProvider())){
            throw new AddressException("Invalid message.provider: ${"+object.getServiceProvider() + "}, should be a valid Tron address");
        }
        return true;
    }
}