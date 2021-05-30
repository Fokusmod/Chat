package ChatClientHomework;

import ChatClientHomework.network.ChatMessageService;
import ChatClientHomework.network.ChatMessageServiceImpl;
import ChatClientHomework.network.MessageProcessor;
import ServerChat.Auch.AuthService;
import ServerChat.Auch.SimpleAuthService;
import common.ChatMessage;
import common.MessageType;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class MainChatController implements Initializable, MessageProcessor {


    private final String PUBLIC = "PUBLIC";
    public TextArea chatArea;
    public ListView onlineUsers;
    public TextField inputField;
    public Button btnSendMessage;
    public TextField loginField;
    public PasswordField passwordField;
    public Button btnSendAuth;
    public Button registration;
    public Label infoLabel;
    public AnchorPane ChatPaneAuth;
    public AnchorPane ChatPane;
    public Button Cancel;
    public Button CompleteRegistration;
    public Label nickName; //TODO
    public Label infoRegLabel;
    public TextField loginFieldReg;
    public PasswordField passwordFieldReg;
    public TextField nickNameReg;
    public AnchorPane Pane;
    public Button btnCancel;
    public Label loginLabel;
    public Label passLabel;
    public Label nickLabel;
    public AnchorPane Settings;
    public Label labelSettingInfo;
    public Button cancelSetting;
    public Button sendChangePass;
    public TextField oldPass;
    public PasswordField newPass;
    public TextField oldNick;
    public TextField newNick;
    public Button sendChangeNick;
    public TextField settingLogin;
    private ChatMessageService messageService;
    private String currentName;

    public Stage stage1;


    public void mockAction(ActionEvent actionEvent) {
        try {
            throw new RuntimeException("AAAAAAAAAAAAA");
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void exit(ActionEvent actionEvent) {

        System.exit(0);


    }

    public void showAbout(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/about.fxml"));
        Stage stage = new Stage();
        Parent root1 = loader.load();
        Scene scene1 = new Scene(root1);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene1);
        stage.show();


        stage.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                stage.close();
            }
        });

    }


    public void showHelp(ActionEvent actionEvent) throws URISyntaxException, IOException {
        Desktop desktop = Desktop.getDesktop();
        desktop.browse(new URI("https://docs.google.com/document/d/1wr0YEtIc5yZtKFu-KITqYnBtp8KC28v2FEYUANL0YAM/edit?usp=sharing"));
    }


    public void sendMessage(ActionEvent actionEvent) {  //TODO
        String text = inputField.getText();
        if (text.isEmpty()) return;
        ChatMessage msg = new ChatMessage();
        String addressee = (String) this.onlineUsers.getSelectionModel().getSelectedItem();
        if (addressee.equals(PUBLIC)) msg.setMessageType(MessageType.PUBLIC);
        else {
            msg.setMessageType(MessageType.PRIVATE);
            msg.setTo(addressee);
        }
        msg.setFrom(currentName);
        msg.setBody(text);
        messageService.send(msg.marshall());
        chatArea.appendText(String.format("%s пишет:\n%s\n", currentName, text));
        String message1 = ((String.format("%s пишет:\n%s\n", currentName, text)));

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Chat_Client/src/main/resources/Chat_History/" + currentName, true))) {
            bw.write(message1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        inputField.clear();
    }

    private void appendTextToChatArea(ChatMessage msg) {
        if (msg.getFrom().equals(this.currentName)) return;
        String modifier = msg.getMessageType().equals(MessageType.PUBLIC) ? "[pub]" : "[private]";
        String text = String.format("%s [%s] пишет:\n%s\n", modifier, msg.getFrom(), msg.getBody());
//        String message2 = String.format("%s [%s] пишет:\n%s\n", modifier, msg.getFrom(), msg.getBody());

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Chat_Client/src/main/resources/Chat_History/" + currentName, true))) {
            bw.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        chatArea.appendText(text);

    }

    public int lineCount() {
        int lines = 0;
        File history = new File("Chat_Client/src/main/resources/Chat_History/" + currentName);
        if (history.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(history))) {
                while (br.readLine() != null) {
                    lines++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return lines;
        }
        return 0;
    }


    public void historyChat() {
        if (lineCount() == 0) return;
        int count = lineCount();
        System.out.println(count);
        File history = new File("Chat_Client/src/main/resources/Chat_History/" + currentName);
        try (BufferedReader br = new BufferedReader(new FileReader(history))) {
            int start = count - 100;
            for (int i = 0; i < count; i++) {
                String text = br.readLine();
                if (i >= start) {
                    chatArea.appendText(text + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleButtonAction(ActionEvent actionEvent) {
    }


    public void sendAuth(ActionEvent actionEvent) throws IOException {
        try {
            if (!messageService.isConnected())
                messageService.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String log = loginField.getText();
        String pass = passwordField.getText();
        if (log.isEmpty() || pass.isEmpty()) {
            infoLabel.setText("Поля не должны быть пустыми.");
            infoLabel.setTextFill(Color.BLACK);
            infoLabel.setStyle("-fx-background-color: Tomato ;");
            return;
        }
        ChatMessage msg = new ChatMessage();
        msg.setMessageType(MessageType.SEND_AUTH);
        msg.setLogin(log);
        msg.setPassword(pass);
        messageService.send(msg.marshall());
        loginField.clear();
        passwordField.clear();


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.messageService = new ChatMessageServiceImpl("localhost", 1300, this);

    }

    @Override
    public void processMessage(String msg) {
        Platform.runLater(() -> {
                    ChatMessage message = ChatMessage.unmarshall(msg);

                    switch (message.getMessageType()) {
                        case AUTH_FAILED:
                        case AUTH_DOUBLE:
                            infoLabel.setText(message.getBody());
                            infoLabel.setTextFill(Color.BLACK);
                            infoLabel.setStyle("-fx-background-color: Tomato ;");
                            break;
                        case AUTH_CONFIRM: {
                            infoLabel.setText(message.getBody());
                            infoLabel.setTextFill(Color.BLACK);
                            infoLabel.setStyle("-fx-background-color: Lime ;");
                            this.currentName = message.getNickName();
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    ChatPane.setVisible(true);
                                    ChatPaneAuth.setVisible(false);
                                }
                            }, 2000);
//                            App.stage1.setTitle(currentName);
                            nickLabel.setText(currentName);
                            historyChat();
                            break;
                        }
                        case SEND_REGISTRATION_FAILED:
                            infoRegLabel.setText(message.getBody());
                            infoRegLabel.setTextFill(Color.BLACK);
                            infoRegLabel.setStyle("-fx-background-color: Tomato ;");
                            break;
                        case SEND_REGISTRATION_CONFIRM:
                            infoRegLabel.setText(message.getBody());
                            infoRegLabel.setTextFill(Color.BLACK);
                            infoRegLabel.setStyle("-fx-background-color: Lime ;");
                            loginFieldReg.setVisible(false);
                            nickNameReg.setVisible(false);
                            passwordFieldReg.setVisible(false);
                            loginLabel.setVisible(false);
                            passLabel.setVisible(false);
                            nickLabel.setVisible(false);
                            Cancel.setVisible(false);
                            CompleteRegistration.setVisible(false);
                            btnCancel.setVisible(true);
                            break;
                        case PUBLIC, PRIVATE:
                            appendTextToChatArea(message);

                            break;
                        case CLIENT_LIST:
                            refreshOnlineUsers(message);
                            break;
                        case CHANGE_PASSWORD_FAILED:
                        case CHANGE_USERNAME_FAILED:
                            labelSettingInfo.setText(message.getBody());
                            labelSettingInfo.setTextFill(Color.BLACK);
                            labelSettingInfo.setStyle("-fx-background-color: Tomato ;");
                            break;
                        case CHANGE_USERNAME_CONFIRM:
                        case CHANGE_PASSWORD_CONFIRM:
                            labelSettingInfo.setText(message.getBody());
                            labelSettingInfo.setTextFill(Color.BLACK);
                            labelSettingInfo.setStyle("-fx-background-color: Lime ;");
                            break;
                    }
                }
        );
    }

    private void refreshOnlineUsers(ChatMessage message) {
        System.out.println("Строка " + message.getOnlineUsers());
        message.getOnlineUsers().add(0, PUBLIC);
        System.out.println(onlineUsers);
        System.out.println(this.onlineUsers);
        this.onlineUsers.setItems(FXCollections.observableArrayList(message.getOnlineUsers()));
        this.onlineUsers.getSelectionModel().selectFirst();


    }

    public void checkIn(ActionEvent actionEvent) {
    }


    public void registration(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Registration.fxml"));
        Stage stage = new Stage();
        Parent root1 = loader.load();
        Scene scene1 = new Scene(root1);
        stage.setScene(scene1);
        stage.show();

    }

    public void cancel(ActionEvent actionEvent) {
        Stage stage = (Stage) Cancel.getScene().getWindow();
        stage.close();
    }

    public void sendRegistration(ActionEvent actionEvent) {
        if (!messageService.isConnected())
            messageService.connect();

        String login = loginFieldReg.getText();
        String pass = passwordFieldReg.getText();
        String nick = nickNameReg.getText();
        if (login.isEmpty() || pass.isEmpty() || nick.isEmpty()) {
            infoRegLabel.setText("Все поля при регистрации должны быть заполнены!");
            infoRegLabel.setTextFill(Color.BLACK);
            infoRegLabel.setStyle("-fx-background-color: Tomato ;");
            return;
        }

        ChatMessage msg = new ChatMessage();
        msg.setMessageType(MessageType.SEND_REGISTRATION);
        msg.setLogin(login);
        msg.setPassword(pass);
        msg.setNickName(nick);
        messageService.send(msg.marshall());
        passwordFieldReg.clear();


    }

    public void showSettings(ActionEvent actionEvent) {
        ChatPane.setVisible(false);
        Settings.setVisible(true);

    }

    public void sendChangeNickname(ActionEvent actionEvent) {
//        try {
//            if (!messageService.isConnected())
//                messageService.connect();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        String oldName = oldNick.getText();
        String newName = newNick.getText();
        if (oldName.isEmpty() || newName.isEmpty()) {
            labelSettingInfo.setText("Поля не должны быть пустыми.");
            labelSettingInfo.setTextFill(Color.BLACK);
            labelSettingInfo.setStyle("-fx-background-color: Tomato ;");
            return;
        }
        ChatMessage msg = new ChatMessage();
        msg.setMessageType(MessageType.CHANGE_USERNAME);
        msg.setOldNick(oldName);
        msg.setNewNick(newName);
        messageService.send(msg.marshall());
        newNick.clear();
        oldNick.clear();
    }

    public void sendChangePass(ActionEvent actionEvent) {
        String oldPassword = oldPass.getText();
        String newPassword = newPass.getText();
        String login = settingLogin.getText();
        if (oldPassword.isEmpty() || newPassword.isEmpty() || login.isEmpty()) {
            labelSettingInfo.setText("Поля не должны быть пустыми.");
            labelSettingInfo.setTextFill(Color.BLACK);
            labelSettingInfo.setStyle("-fx-background-color: Tomato ;");
            return;
        }
        ChatMessage msg = new ChatMessage();
        msg.setMessageType(MessageType.CHANGE_PASSWORD);
        msg.setLogin(login);
        msg.setOldPassword(oldPassword);
        msg.setNewPassword(newPassword);
        messageService.send(msg.marshall());
        settingLogin.clear();
        newPass.clear();
        oldPass.clear();

    }

    public void cancelSetting(ActionEvent actionEvent) {
        Settings.setVisible(false);
        ChatPane.setVisible(true);
    }

}

//case AUTH_DOUBLE:
//        infoLabel.setText(message.getBody());
//        infoLabel.setTextFill(Color.BLACK);
//        infoLabel.setStyle("-fx-background-color: Tomato ;");
//        break;
//        case AUTH_CONFIRM: {
//        infoLabel.setText(message.getBody());
//        infoLabel.setTextFill(Color.BLACK);
//        infoLabel.setStyle("-fx-background-color: Lime ;");