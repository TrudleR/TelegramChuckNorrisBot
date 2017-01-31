package trudlerbot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class TrudlerBot {

    public static void main(String...args) {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            ApiContextInitializer.init();
            telegramBotsApi.registerBot(new MessageHandler());
//            telegramBotsApi.re
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
