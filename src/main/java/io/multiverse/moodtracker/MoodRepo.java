package io.multiverse.moodtracker;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

interface MoodRepo extends JpaRepository<Mood, Integer> {
    List<Mood> findByUserId(String userId);
}
