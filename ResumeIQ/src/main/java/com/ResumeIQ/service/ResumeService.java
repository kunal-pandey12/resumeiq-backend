package com.ResumeIQ.service;

import com.ResumeIQ.entity.Resume;
import com.ResumeIQ.entity.User;
import com.ResumeIQ.repo.ResumeRepo;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepo resumeRepo;

    // Resume files yahan save hongi server pe
    private final String UPLOAD_DIR = "C:/Users/Kunal pandey/OneDrive/Desktop/ResumeIQ/uploads/";

    public Resume uploadResume(MultipartFile file, User user)
            throws IOException {

        // Folder nahi hai toh banao
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // File server pe save karo
        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath,
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        // Check karo file sahi se save hui ya nahi
        File pdfFile = filePath.toFile();
        System.out.println("File exists: " + pdfFile.exists());
        System.out.println("File path: " + pdfFile.getAbsolutePath());

        // PDF se text nikalo
        String extractedText = extractTextFromPdf(pdfFile);
        System.out.println("Extracted text length: " +
                (extractedText != null ? extractedText.length() : "NULL"));

        // Database mein save karo
        Resume resume = new Resume();
        resume.setUser(user);
        resume.setFileName(fileName);
        resume.setFilePath(filePath.toString());
        resume.setExtractedText(extractedText);

        return resumeRepo.save(resume);
    }

    // PDF ke andar se text nikalta hai PDFBox se
    private String extractTextFromPdf(File file) throws IOException {
        PDDocument document = Loader.loadPDF(file);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();
        return text;
    }

    // Us user ke saare resumes lo database se
    public java.util.List<Resume> getResumesByUser(User user) {
        return resumeRepo.findByUserId(user.getId());
    }
}