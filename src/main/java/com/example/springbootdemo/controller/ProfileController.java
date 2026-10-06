package com.example.springbootdemo.controller;

import com.example.springbootdemo.dto.UserProfileResponse;
import com.example.springbootdemo.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile() {
        return ResponseEntity.ok(profileService.getCurrentUserProfile());
    }
}
