package trudlerbot;

import org.telegram.telegrambots.api.methods.GetUserProfilePhotos;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import trudlerbot.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class MessageHandler extends TelegramLongPollingBot {

    private ChuckNorrisPool chuckNorrisPool = new ChuckNorrisPool();
    private Bullies bullies = new Bullies();

    private Integer[] authorOfLastThreeMessages = new Integer[10];

    private void handleIncomingMessage(Message message) throws TelegramApiException {
        final SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId().toString());
        final String lowerMessage = message.getText().toLowerCase();
        final Optional<String> bully = bullies.grabBullyIfExist(lowerMessage);

        if (isThisUserASpammer(message)) {
            reactToSpammer(message, messageToSend);
        } else if (bully.isPresent() && (lowerMessage.contains("chuck") || lowerMessage.contains("norris"))) {
            reactToPersonalInsult(message, messageToSend, message.getMessageId());
        } else if (bully.isPresent()) {
            reactToHarassment(message, messageToSend, bully.get());
        } else if (lowerMessage.contains("chuck") || lowerMessage.contains("norris")) {
            provideEpicChuckNorrisJoke(message, messageToSend);
        } else if (lowerMessage.contains("trudle")) {
            reactIfTrudlerIsAsleep(messageToSend);
        } else if (Math.random() < 0.005) {
            sendRandomMessageToStranger(messageToSend, message.getMessageId());
        } else {
            return;
        }
        logMessage(message);
    }

    private void reactToSpammer(Message message, SendMessage messageToSend) throws TelegramApiException {
        messageToSend.setReplyToMessageId(message.getMessageId());
        messageToSend.setText(CounterSpammer.getRandomCounter());
        sendMessage(messageToSend);
    }

    private boolean isThisUserASpammer(Message message) {
        final Integer id = message.getFrom().getId();

        for (int i=authorOfLastThreeMessages.length-1; i > 0;) {
            authorOfLastThreeMessages[i] = authorOfLastThreeMessages[--i];
        }
        authorOfLastThreeMessages[0] = id;

        for (Integer author : authorOfLastThreeMessages) {
            if (!authorOfLastThreeMessages[0].equals(author)) return false;
        }
        return true;
    }

    private void reactToPersonalInsult(Message message, SendMessage messageToSend, Integer messageId) throws TelegramApiException {
        messageToSend.setText(CounterAttack.getRandomCounter());
        messageToSend.setReplyToMessageId(messageId);
        sendMessage(messageToSend);
    }

    private void sendRandomMessageToStranger(SendMessage messageToSend, Integer messageId) throws TelegramApiException {
        messageToSend.setText(Denials.getRandomDenial());
        messageToSend.setReplyToMessageId(messageId);

        sendMessage(messageToSend);
    }

    /**
     * Refactoring: Should be modified to take the day from the past -> 22:30 until today -> 06:00. Would save lines of code.
     *
     * @param messageToSend
     * @throws TelegramApiException
     */
    private void reactIfTrudlerIsAsleep(SendMessage messageToSend) throws TelegramApiException {
        final LocalDateTime now = LocalDateTime.now();
        LocalDateTime minRange = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 22, 30);
        final LocalDateTime maxRange = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 6, 30);

        final LocalDateTime startOfCurrentDay = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0);

        if (now.isAfter(startOfCurrentDay) && now.isBefore(maxRange) ||
                now.isAfter(minRange) && now.isBefore(startOfCurrentDay.plusDays(1))) {
            messageToSend.setText("Ssshhh, TrudleR is sleeping! >:-(");
            sendMessage(messageToSend);
        }
    }

    private void provideEpicChuckNorrisJoke(Message message, SendMessage messageToSend) throws TelegramApiException {
        messageToSend.setText(chuckNorrisPool.fetchRandomJoke());
        sendMessage(messageToSend);
        logMessage(message);
    }

    private void reactToHarassment(Message message, SendMessage messageToSend, String bully) throws TelegramApiException {
        GetUserProfilePhotos a = new GetUserProfilePhotos();
        a.setUserId(message.getFrom().getId());
        final List<List<PhotoSize>> photos = getUserProfilePhotos(a).getPhotos();
        if (photos.isEmpty()) {
            messageToSend.setText("How can you call someone " + bully + " while you are too cowardly to even provide a profile pic.");
            sendMessage(messageToSend);
        } else {
            SendPhoto sp = new SendPhoto();
            sp.setChatId(message.getChatId().toString());
            int photoIndex = (int) (Math.random() * (double) photos.size());
//            int photoIndex = 0;

            sp.setPhoto(photos.get(photoIndex).get(0).getFileId());
            messageToSend.setText("My my, such words from an individual that uses such profile pics...");
            sendMessage(messageToSend);
            sendPhoto(sp);
        }
    }

    private void logMessage(Message message) {
        final Optional<String> chatTitleOptional = Optional.ofNullable(message).map(Message::getChat).map(Chat::getTitle);
        final Optional<String> firstNameOptional = Optional.ofNullable(message).map(Message::getFrom).map(User::getFirstName);
        final Optional<String> lastNameOptional = Optional.ofNullable(message).map(Message::getFrom).map(User::getLastName);
        final Optional<String> userNameOptional = Optional.ofNullable(message).map(Message::getFrom).map(User::getUserName);

        final String chatTitle = chatTitleOptional.orElse("[CHAT_NAME]");
        final String firstName = firstNameOptional.orElse("[FIRST_NAME]");
        final String lastName = lastNameOptional.orElse("[LAST_NAME]");
        final String userName = userNameOptional.orElse("[USER_NAME]");

        String messageAddress = chatTitle + ": " + firstName + " " + lastName + " (" +userName + "): " + message.getText();
        System.out.println(messageAddress);
    }

    @Override
    public void onUpdateReceived(Update update) {
        final Message message = update.getMessage();
        if (message != null && message.hasText()) {
            try {
                handleIncomingMessage(message);
            } catch (TelegramApiException exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "TrudleR_bot";
    }

    @Override
    public String getBotToken() {
        return "216449472:AAHsJMmDszxHE1uIL69TqCj_JhM577h1rPA";
    }
}
