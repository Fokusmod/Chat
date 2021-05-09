package ServerChat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    static int clientCounter = 0;
    private  int clientNumber;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Server server;

    public ClientHandler(Socket socket, Server server) {
        try {
            this.server = server;
            this.socket = socket;
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
            this.clientNumber = ++clientCounter;

            System.out.println("Client handler created!!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handle() {
        new Thread(()-> {
            try {
                while (!Thread.currentThread().isInterrupted() || socket.isConnected()) {
                    String msg = inputStream.readUTF();
                    System.out.printf("Client#%d: %s\n", this.clientNumber, msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
