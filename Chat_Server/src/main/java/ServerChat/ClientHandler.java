package ServerChat;

import ServerChat.Auch.AuthService;
import common.ChatMessage;
import common.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

public class ClientHandler {
    private Socket socket;
    private ChatServer chatServer;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private String currentUsername;

    public ClientHandler(Socket socket, ChatServer chatServer) {
        try {
            this.chatServer = chatServer;
            this.socket = socket;
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
            System.out.println("Обработчик клиентов создан!!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void handle() {
        new Thread(() -> {
            try {
                authenticate();
                readMessages();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void readMessages() throws IOException {
        try {
            while (!Thread.currentThread().isInterrupted() || !socket.isClosed()) {
                String msg = inputStream.readUTF();
                ChatMessage message = ChatMessage.unmarshall(msg);
                message.setFrom(this.currentUsername);
                switch (message.getMessageType()) {
                    case PUBLIC -> chatServer.sendBroadcastMessage(message);
                    case PRIVATE -> chatServer.sendPrivateMessage(message);
                }
            }
        } catch (IOException e) {
            socket.close();
        }
    }

    public void sendMessage(ChatMessage message) {
        try {
            outputStream.writeUTF(message.marshall());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentName() {
        return this.currentUsername;
    }

    private void authenticate() {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    ChatMessage response = new ChatMessage();
                    response.setMessageType(MessageType.ERROR);
                    response.setBody("Время для подключения вышло! Вы были отключены.");
                    sendMessage(response);
                    Thread.sleep(50);
                    socket.close();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 20000);

        System.out.println("Запущена аутентификация клиента...");
        try {
            while (true) {
                String authMessage = inputStream.readUTF();
                System.out.println("Auth received");
                ChatMessage msg = ChatMessage.unmarshall(authMessage);
                String username = chatServer.getAuthService().getUsernameByLoginAndPassword(msg.getLogin(), msg.getPassword());
                ChatMessage response = new ChatMessage();

                if (username == null) {
                    response.setMessageType(MessageType.ERROR);
                    response.setBody("Неправильное имя или пароль!");
                    System.out.println("Неверные учетные данные");
                } else if (chatServer.isUserOnline(username)) {
                    response.setMessageType(MessageType.ERROR);
                    response.setBody("Double auth!");
                    System.out.println("Double auth!");
                } else {
                    response.setMessageType(MessageType.AUTH_CONFIRM);
                    response.setBody(username);
                    currentUsername = username;
                    chatServer.subscribe(this);
                    System.out.println("Subscribed");
                    sendMessage(response);
                    timer.cancel();
                    break;
                }
                sendMessage(response);
            }
        } catch (IOException e) {  //TODO
            try {
                socket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void closeHandler() {
        try {
            chatServer.unsubscribe(this);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentUsername() {
        return currentUsername;
    }
}