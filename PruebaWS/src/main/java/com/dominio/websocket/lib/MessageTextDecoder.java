package com.dominio.websocket.lib;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.dominio.entity.Message;

public class MessageTextDecoder implements Decoder.Text<Message>{

	@Override
	public void init(EndpointConfig endpointConfig) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Message decode(String s) throws DecodeException {
		 JsonObject jsonObject = Json.createReader(new StringReader(s)).readObject();
			    Message message = new Message();
			    message.setText(jsonObject.getString("text"));
			    return message;
	}

	@Override
	public boolean willDecode(String s) {
		try {
		      // Check if incoming message is valid JSON
		      Json.createReader(new StringReader(s)).readObject();
		      return true;
		    } catch (Exception e) {
		      return false;
		    }
	}

}
