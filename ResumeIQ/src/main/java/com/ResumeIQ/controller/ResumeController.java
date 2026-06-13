package com.ResumeIQ.controller;

import com.ResumeIQ.entity.Resume;
import com.ResumeIQ.entity.User;
import com.ResumeIQ.service.ResumeService;
import com.ResumeIQ.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// Resume upload aur fetch ke liye APIs
@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private UserService userService;

    // Resume upload karo — token se user pata chalega
    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Token se email nikala, user find kiya
            User user = (User) userService
                    .loadUserByUsername(userDetails.getUsername());

            Resume resume = resumeService.uploadResume(file, user);
            return ResponseEntity.ok("Resume uploaded: "
                    + resume.getFileName());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Upload failed: " + e.getMessage());
        }
    }

    // User ke saare resumes liya
    @GetMapping("/my")
    public ResponseEntity<List<Resume>> getMyResumes(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = (User) userService
                .loadUserByUsername(userDetails.getUsername());
        List<Resume> resumes = resumeService.getResumesByUser(user);
        return ResponseEntity.ok(resumes);
    }
}