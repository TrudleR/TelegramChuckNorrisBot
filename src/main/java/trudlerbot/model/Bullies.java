package trudlerbot.model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Bullies {

    private static final List<String> BULLIES = Arrays.asList(
            "asshole",
            "idiot",
            "mang",
            "fag",
            "mofo",
            "nigger",
            "fucktard",
            "prick"
    );

    public Optional<String> grabBullyIfExist(String lowerMessage) {
        return BULLIES.stream().filter(bully -> lowerMessage.contains(bully)).findFirst();
    }
}
