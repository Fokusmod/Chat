package ServerChat.Auch;

import java.sql.SQLException;

public interface AuthService {
    void start();
    void stop();
    String getUsernameByLoginAndPassword(String login, String password);
    String changeUsername(String newName,String oldName);
    String changePassword(String newPassword, String currentLogin, String oldPassword);
    boolean newUser(String login, String password,String username);
    boolean isStart() throws SQLException;

}
