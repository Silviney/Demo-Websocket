package com.dominio.websocket.lib;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.dominio.entity.Message;


public class MessageTextEncoder implements Encoder.Text<Message> {

	@Override
	public void init(EndpointConfig endpointConfig) {
	}

	@Override
	public void destroy() {
	}

	@Override
	public String encode(Message message) throws EncodeException {
		
		/*
		JSONObject json = new JSONObject();
		json.put("text", message.getText());
		return json.toString();
		*/
		 JsonObject jsonObject = (JsonObject) Json.createObjectBuilder()
			        .add("text", message.getText());
			    return jsonObject.toString();

		
	}
	
	/*
	 public static String protoBufToJson(Message message) {
		return JsonFormat.printToString(message);
	}
	*/
	
}
