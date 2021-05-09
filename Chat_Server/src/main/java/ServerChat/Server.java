package ServerChat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 8000;

    public void start() {
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен.");

            while (true) {
                System.out.println("Ждём подключения!...");
                Socket socket = serverSocket.accept();
                System.out.println("Пользователь подключился");
                new ClientHandler(socket,this).handle();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
