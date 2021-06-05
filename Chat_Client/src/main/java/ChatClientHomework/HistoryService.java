package ChatClientHomework;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryService {
    public static final int HISTORY_SIZE = 100;
    String login;
    File history;
    String historyPath;

    public HistoryService(String login) {
        this.historyPath = "Chat_Client/src/main/resources/Chat_History/";
        this.login = login;
        this.history = new File(historyPath + login + ".txt");
        if (!history.exists()) {
            File path = new File(historyPath);
            path.mkdirs();
        }
    }

    public List<String> readHistory () {
        if (!history.exists()) return List.of("История пока отсутствует.");
        List<String> result = null;
        if (history.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(history))) {
                String historyString;
                List<String> historyStrings = new ArrayList<>();
                while ((historyString = reader.readLine()) != null) {
                    historyStrings.add(historyString);
                }
                if (historyStrings.size() <= HISTORY_SIZE) {
                    result = historyStrings;
                }
                if (historyStrings.size() > HISTORY_SIZE) {
                    int firstIndex = historyStrings.size() - HISTORY_SIZE;
                    result = new ArrayList<>(100);

                    for (int counter = firstIndex - 1; counter < historyStrings.size(); counter++) {
                        result.add(historyStrings.get(counter));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("History for " + result.size());
        return result;
    }

    public void writeHistory(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(history, true))) {
            writer.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
