package com.ResumeIQ.dto;
import com.ResumeIQ.entity.Role;
import lombok.Data;

@Data
public class UserDto {

    private String name;
    private String email;
    private String password;
    private Role role;
}
