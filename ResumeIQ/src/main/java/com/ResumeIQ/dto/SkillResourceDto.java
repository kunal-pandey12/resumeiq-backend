package com.ResumeIQ.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillResourceDto {
    private String skill;
    private String priority;
    private List<String> resources;
}
