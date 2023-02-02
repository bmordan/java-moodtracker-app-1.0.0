package io.multiverse.moodtracker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class MoodtrackerEntityTest {
    
    @Autowired
    private MoodRepo repo;

    @Test
    public void canCreateMood() {
        Mood mood = new Mood();
        mood.setScore(7);
        mood.setUserId("1");
        Mood moodRecord = repo.save(mood);

        assertEquals("1", moodRecord.getUserId());
    }

}
