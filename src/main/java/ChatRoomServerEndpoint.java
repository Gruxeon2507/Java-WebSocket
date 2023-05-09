
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jakarta.websocket.CloseReason;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/chat/{room-id}")
public class ChatRoomServerEndpoint extends Endpoint {

    // Map of chat room ID to list of active sessions
    private static final Map<String, List<Session>> chatRooms = new ConcurrentHashMap<>();

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        // Get the chat room ID from the URI
        final String roomId = session.getPathParameters().get("room-id");

        // If the chat room doesn't exist, create a new list of sessions for it
        chatRooms.putIfAbsent(roomId, new ArrayList<Session>());

        // Add the session to the list of active sessions for the chat room
        chatRooms.get(roomId).add(session);

        // Create a message handler for incoming messages
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                // Broadcast message to all connected sessions in the same chat room
                List<Session> roomSessions = chatRooms.get(roomId);
                for (Session roomSession : roomSessions) {
                    try {
                        roomSession.getBasicRemote().sendText(message);
                        System.out.println(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        // Get the chat room ID from the URI
        String roomId = session.getPathParameters().get("room-id");

        // Remove the session from the list of active sessions for the chat room
        chatRooms.get(roomId).remove(session);

        // If there are no more active sessions for the chat room, remove the chat room from the map
        if (chatRooms.get(roomId).isEmpty()) {
            chatRooms.remove(roomId);
        }
    }

}
