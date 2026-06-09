package com.ResumeIQ.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResumeResponseDto {
    private Long id;
    private String fileName;
    private String extractedText;
    private LocalDateTime uploadedAt;
}