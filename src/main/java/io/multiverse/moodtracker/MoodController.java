package io.multiverse.moodtracker;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class MoodController {
    
    @Autowired
    private MoodRepo moodRepo;
    
    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/moods")
    public String getMoods(Model model, @AuthenticationPrincipal OidcUser principal) {
        model.addAttribute("user", principal.getClaims());
        model.addAttribute("moods", moodRepo.findByUserId(principal.getName()));
        return "moods";
    }
    
    @PostMapping("/moods")
    public String createMood(Model model, Mood mood, @AuthenticationPrincipal OidcUser principal) {
        moodRepo.save(mood);
        model.addAttribute("user", principal.getClaims());
        model.addAttribute("moods", moodRepo.findByUserId(principal.getName()));
        return "moods";
    }
}
