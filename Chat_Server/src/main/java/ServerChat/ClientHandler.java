package ServerChat;

import ServerChat.Auch.AuthService;
import common.ChatMessage;
import common.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
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

//            try {
            auth();
//                readMessages();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }).start();
    }

    public void auth() {
        try {
            while (true) {
                String msg = inputStream.readUTF();
                ChatMessage message = ChatMessage.unmarshall(msg);
//                message.setFrom(this.currentUsername);
                switch (message.getMessageType()) {
                    case SEND_AUTH:
                        String nickname = chatServer.getAuthService().getUsernameByLoginAndPassword(message.getLogin(), message.getPassword());
                        if (nickname == null) {
                            ChatMessage failed = new ChatMessage();
                            failed.setMessageType(MessageType.AUTH_FAILED);
                            failed.setBody("Пользователя с таким логином или паролем не существует. Введите корректные данные");
                            sendMessage(failed);
                        } else if (chatServer.isUserOnline(nickname)) {
                            ChatMessage failed = new ChatMessage();
                            failed.setMessageType(MessageType.AUTH_DOUBLE);
                            failed.setBody("Пользователь с таким логином уже в сети.");
                            sendMessage(failed);
                        } else {
                            ChatMessage confirm = new ChatMessage();
                            confirm.setMessageType(MessageType.AUTH_CONFIRM);
                            confirm.setNickName(nickname);
                            confirm.setBody("Успешное подключение");
                            sendMessage(confirm);
                            currentUsername = nickname;
                            chatServer.subscribe(this);
                            System.out.println("Subscribed");
                            break;
                        }
                    case SEND_REGISTRATION:
                        if (chatServer.getAuthService().newUser(message.getLogin(), message.getPassword(), message.getNickName())) {
                            ChatMessage confirm = new ChatMessage();
                            confirm.setMessageType(MessageType.SEND_REGISTRATION_CONFIRM);
                            confirm.setBody("Регистрация прошла успешно");
                            sendMessage(confirm);
                        } else {
                            ChatMessage failed = new ChatMessage();
                            failed.setMessageType(MessageType.SEND_REGISTRATION_FAILED);
                            failed.setBody("Введите корректные данные. Обратите внимание на то что логин и ник не могут быть одинаковыми.");
                            sendMessage(failed);
                        }
                        break;
                    case PUBLIC:
                        chatServer.sendBroadcastMessage(message);
                        break;
                    case PRIVATE:
                        chatServer.sendPrivateMessage(message);
                        break;
                    case CHANGE_USERNAME:
                        String name = chatServer.getAuthService().changeUsername(message.getNewNick(), message.getOldNick());
                        System.out.println("Сообщение с бд" + name);
                        if (name.equalsIgnoreCase(message.getNewNick())) {
                            ChatMessage confirm = new ChatMessage();
                            confirm.setMessageType(MessageType.CHANGE_USERNAME_CONFIRM);
                            confirm.setBody("Успешная смена ника!");
                            sendMessage(confirm);
                        } else {
                            ChatMessage failed = new ChatMessage();
                            failed.setMessageType(MessageType.CHANGE_USERNAME_FAILED);
                            failed.setBody("Введенный вами старый никнейм не совпадает.");
                            sendMessage(failed);
                        }
                        break;
                    case CHANGE_PASSWORD:
                        String pass = chatServer.getAuthService().changePassword(message.getNewPassword(),message.getLogin(),message.getOldPassword());
                        System.out.println("Сообщение с бд" + message.getNewPassword() + message.getLogin() +  message.getOldPassword() + "" + pass);
                        if (pass.equalsIgnoreCase(message.getNewPassword())) {
                            System.out.println(pass);
                            ChatMessage confirm = new ChatMessage();
                            confirm.setMessageType(MessageType.CHANGE_PASSWORD_CONFIRM);
                            confirm.setBody("Успешная смена пароля!");
                            sendMessage(confirm);
                        } else {
                            ChatMessage failed = new ChatMessage();
                            failed.setMessageType(MessageType.CHANGE_PASSWORD_FAILED);
                            failed.setBody("Введенный вами данные не совпадают.");
                            sendMessage(failed);
                        }
                        break;


//                        case PUBLIC -> chatServer.sendBroadcastMessage(message);
//                        case PRIVATE -> chatServer.sendPrivateMessage(message);

                }
            }
        } catch (IOException e) {
            closeHandler();
            System.out.println("Клиент был отключен.");
        }
    }


    private void readMessages() throws IOException {
        try {
            while (!Thread.currentThread().isInterrupted() || socket.isConnected()) {
                String msg = inputStream.readUTF();
                ChatMessage message = ChatMessage.unmarshall(msg);
                message.setFrom(this.currentUsername);
                switch (message.getMessageType()) {
                    case PUBLIC:
                        chatServer.sendBroadcastMessage(message);
                        break;
                    case PRIVATE:
                        chatServer.sendPrivateMessage(message);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeHandler();
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
//        Timer timer = new Timer(true);
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    ChatMessage response = new ChatMessage();
//                    response.setMessageType(MessageType.ERROR);
//                    response.setBody("Время для подключения вышло! Вы были отключены.");
//                    sendMessage(response);
//                    Thread.sleep(50);
//                    socket.close();
//                } catch (IOException | InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 200000);
//
//        System.out.println("Запущена аутентификация клиента...");
//        try {
//            while (true) {
//                String authMessage = inputStream.readUTF();
//                System.out.println("Auth received");
//                ChatMessage msg = ChatMessage.unmarshall(authMessage);
//                String username = chatServer.getAuthService().getUsernameByLoginAndPassword(msg.getLogin(), msg.getPassword());
//                System.out.println(authMessage);
//                System.out.println(username + msg.getLogin() + msg.getPassword());
//                ChatMessage response = new ChatMessage();
//
//                if (username == null) {
//                    response.setMessageType(MessageType.ERROR);
//                    response.setBody("Неправильное имя или пароль!");
//                    System.out.println("Неверные учетные данные");
//                } else if (chatServer.isUserOnline(username)) {
//                    response.setMessageType(MessageType.ERROR);
//                    response.setBody("Double auth!");
//                    System.out.println("Double auth!");
//                } else {
//                    response.setMessageType(MessageType.AUTH_CONFIRM);
//                    response.setBody(username);
//                    currentUsername = username;
//                    chatServer.subscribe(this);
//                    System.out.println("Subscribed");
//                    sendMessage(response);
//                    timer.cancel();
//                    break;
//                }
//                sendMessage(response);
//            }
//        } catch (IOException e) {  //TODO
//            try {
//                socket.close();
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//        }
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