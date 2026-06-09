package com.ResumeIQ.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    // password nahi hai! ye retrun hoga servicelogic se
}