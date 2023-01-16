/**
 * 
 */
package com.dominio.impl;



import org.apache.log4j.Logger;
/**
 * @author diego.molina
 *
 */
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint(value = "/testWebSocket/{token}")
public class WebSocketImpl {

	private static final Logger LOG = Logger.getLogger(WebSocketImpl.class);
	/*
	 * Mantem cada objeto webSocket e guarda no ConcurrentHashMap seguro para
	 * subprocesos com clave-valor,
	 */
	private static ConcurrentHashMap<String, WebSocketImpl> concurrentHashMap = new ConcurrentHashMap<>(12);

	/**
	 * Objeto de conversação
	 **/
	private Session session;

	/*
	 * ativa quando o cliente cria uma conexão
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam("token") String token) {
		// Cada vez que se estabelece uma nova conexão, armazena o ID do cliente
		// atua como chave e este como valor no mapa
		this.session = session;
		concurrentHashMap.put(token, this);
//		System.out.println("Open a websocket. token={}" + token);
		LOG.info("Open a websocket. token={}" + token);
	}

	/**
	 * ativa quando a conexão do cliente está fechada
	 **/
	@OnClose
	public void onClose(Session session, @PathParam("token") String token) {
		// quando se encerra a conexão do cliente, elimina os pares chave-valor
		// armazenados no mapa
		concurrentHashMap.remove(token);
//		System.out.println("Close a websocket, concurrentHashMap remove sessionId= {}" + token);
		LOG.info("Close a websocket, concurrentHashMap remove sessionId= {}" + token);
	}

	/**
	 * ativa quando se recebe uma mensagem do cliente
	 * @throws Exception 
	 */
	@OnMessage
	public void onMessage(String message, @PathParam("token") String token) throws Exception {
//		System.out.println("receive a message from client id={},msg={}" + token + " " + message);
		LOG.info("receive a message from client id={},msg={}" + token + " " + message);
	}

	/**
	 * ativa quando ocorre uma conexão anormal
	 */
	@OnError
	public void onError(Session session, Throwable error) {
//		System.out.println("Error while websocket. " + error);
		LOG.error("Error while websocket. " + error);
	}

	/**
	 * Enviar mensagem ao cliente especificado
	 * 
	 * @param token
	 * @param message
	 */
	public void sendMessage(String token, String message) throws Exception {
		// Segundo a identificação, obtem o objeto webSocket armazenado do mapa
		WebSocketImpl webSocketProcess = concurrentHashMap.get(token);
		if (!ObjectUtils.isEmpty(webSocketProcess)) {
			// A mensagem apenas pode enviar quando o cliente está no estado aberto
			if (webSocketProcess.session.isOpen()) {
				webSocketProcess.session.getBasicRemote().sendText(message);
			} else {
//				System.out.println("websocket session={} is closed " + token);
				LOG.error("websocket session={} is closed " + token);
			}
		} else {
//			System.out.println("websocket session={} is not exit " + token);
			LOG.error("websocket session={} is not exit " + token);
		}
	}

	/**
	 * Enviar mensagns a todos os clientes
	 * 
	 */
	public void sendAllMessage(String msg) throws Exception {
//		System.out.println("online client count={}" + concurrentHashMap.size());
		LOG.info("online client count={}" + concurrentHashMap.size());
		Set<Map.Entry<String, WebSocketImpl>> entries = concurrentHashMap.entrySet();
		for (Map.Entry<String, WebSocketImpl> entry : entries) {
			String cid = entry.getKey();
			WebSocketImpl webSocketProcess = entry.getValue();
			boolean sessionOpen = webSocketProcess.session.isOpen();
			if (sessionOpen) {
//				  webSocketProcess.session.getAsyncRemote().sendText(msg);
				webSocketProcess.session.getBasicRemote().sendText(msg);
			} else {
//				System.out.println("cid={} is closed,ignore send text" + cid);
				LOG.info("cid={} is closed,ignore send text" + cid);
			}
		}
	}
	
	public static String readFileAsString(String file) throws Exception {
		byte[] utf8 = new String(Files.readAllBytes(Paths.get(file)), StandardCharsets.ISO_8859_1).getBytes("UTF-8");;
		String content = new String(utf8);
		return content;
	}

}
