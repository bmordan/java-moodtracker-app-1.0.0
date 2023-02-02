package io.multiverse.moodtracker;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MoodtrackerClassTest {
    @Test
    public void moodEntityTest() {
        Mood mood = new Mood();
        mood.setScore(7);
        assertEquals(7, mood.getScore());
    }
}
