package ServerChat.Auch;

public interface AuthService {
    void start();
    void stop();
    String getUsernameByLoginAndPassword(String login, String password);
    void changeUsername(String oldName, String newName);
    void changePassword(String username, String oldPassword, String newPassword);
    void newUser(String username, String login, String password);

}
