package trudlerbot.model;

import java.util.Arrays;
import java.util.List;

public class CounterAttack {

    private static final List<String> COUNTER_ATTACKS = Arrays.asList(
            "You bastard... say that again and I kick your head off!",
            "Stop being annoying you dumbass...",
            "You little prick will die as soon as I find time...",
            "Are you aware that you won't survive today?",
            "A fools last words..."
    );

    public static String getRandomCounter() {
        return COUNTER_ATTACKS.get((int) (Math.random() * COUNTER_ATTACKS.size()));
    }

}
