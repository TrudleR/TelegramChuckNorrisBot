package trudlerbot.model;

import java.util.Arrays;
import java.util.List;

public class Denials {

    private static final List<String> DENIALS = Arrays.asList(
            "Haha, for real? :D",
            "I don't think so...",
            "You serious about this?",
            "Oh really..."
    );

    public static String getRandomDenial() {
        return DENIALS.get((int) (Math.random() * DENIALS.size()));
    }
}
