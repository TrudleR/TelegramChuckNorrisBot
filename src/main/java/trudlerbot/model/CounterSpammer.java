package trudlerbot.model;

import java.util.Arrays;
import java.util.List;

public class CounterSpammer {

    private static final List<String> COUNTER_ATTACKS = Arrays.asList(
            "Are you a little kid or what? Stop spamming!",
            "STOP SPAMMING!!",
            "Stop spamming or I roundhouse your face!",
            "Stop Spamming, Potato!"
    );

    public static String getRandomCounter() {
        return COUNTER_ATTACKS.get((int) (Math.random() * COUNTER_ATTACKS.size()));
    }

}
