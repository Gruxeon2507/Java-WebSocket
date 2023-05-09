/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author Nguyen Hoang Minh
 */
@WebSocketHandlerService(path = "/chat")
public class ChatWebSocketHandler implements WebSocketHandler {

    private final ConcurrentLinkedQueue<AtmosphereResource> connectedClients = new ConcurrentLinkedQueue<>();

    @Override
    public void onOpen(WebSocket webSocket) {
        connectedClients.add(webSocket.resource());
    }

    @Override
    public void onClose(WebSocketContainer webSocket) {
        connectedClients.remove(webSocket.resource());
    }

    @Override
    public void onTextMessage(WebSocket webSocket, String message) {
        for (AtmosphereResource client : connectedClients) {
            try {
                client.getWebSocket().write(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
