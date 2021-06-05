package ServerChat;



import ServerChat.Auth.AuthService;
import ServerChat.Auth.SimpleAuthService;
import common.ChatMessage;
import common.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private static final Logger LOGGER = LogManager.getLogger(ChatServer.class);
    private static final int PORT = 1300;
    private List<ClientHandler> listOnlineUsers;
    private AuthService authService;
    private ExecutorService executorService;

    public ChatServer() {
        this.listOnlineUsers = new ArrayList<>();
        this.authService = new SimpleAuthService();
        this.executorService = Executors.newCachedThreadPool();
    }

    public void start() {
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            LOGGER.info("Сервер запущен.");
            authService.start();

            while (true) {
                LOGGER.info("Ждём подключений");
                Socket socket = serverSocket.accept();
                LOGGER.info("Клиент подключился");
                new ClientHandler(socket, this).handle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            authService.stop();
            executorService.shutdown();
        }
    }

    public synchronized void sendListOnlineUsers() {
        ChatMessage msg = new ChatMessage();
        msg.setMessageType(MessageType.CLIENT_LIST);
        msg.setOnlineUsers(new ArrayList<>());
        for (ClientHandler user : listOnlineUsers) {
            msg.getOnlineUsers().add(user.getCurrentName());

        }
        for (ClientHandler user : listOnlineUsers) {
            user.sendMessage(msg);
        }
    }

    public synchronized void sendBroadcastMessage(ChatMessage message) {
        for (ClientHandler user : listOnlineUsers) {
            user.sendMessage(message);
        }
    }

    public void sendPrivateMessage(ChatMessage message) {
        for (ClientHandler user : listOnlineUsers) {
            if (user.getCurrentName().equals(message.getTo()))
            user.sendMessage(message);
        }
    }

    public synchronized boolean isUserOnline(String username) {
        for (ClientHandler user : listOnlineUsers) {
            if (user.getCurrentName().equals(username)) return true;
        }
        return false;
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        listOnlineUsers.add(clientHandler);
        sendListOnlineUsers();
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        listOnlineUsers.remove(clientHandler);
        sendListOnlineUsers();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

}