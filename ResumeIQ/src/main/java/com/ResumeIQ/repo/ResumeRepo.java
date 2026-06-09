package com.ResumeIQ.repo;

import com.ResumeIQ.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepo extends JpaRepository<Resume,Long> {
    List<Resume>findByUserId(Long userId);
}
