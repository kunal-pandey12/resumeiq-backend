package com.ResumeIQ.service;

import com.ResumeIQ.dto.SkillResourceDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

// Gemini AI se baat karne wali service
@Service
public class GeminiService {

    // application.properties se key aur url uthao
    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // Resume ka text bhejo, AI se analysis lo
    public String analyzeResume(String resumeText) {

        // Gemini ko jo prompt bhejna hai
        String prompt = "Analyze this resume and give:\n" +
                "1. ATS Score out of 100\n" +
                "2. Strengths\n" +
                "3. Weaknesses\n" +
                "4. Suggestions to improve\n\n" +
                "Resume:\n" + resumeText;

        // Gemini API ka required JSON format
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();

        part.put("text", prompt);
        content.put("parts", List.of(part));
        requestBody.put("contents", List.of(content));

        // URL mein API key add karo
        String urlWithKey = apiUrl + "?key=" + apiKey;

        // Gemini ko request bhejo aur response lo
        Map response = restTemplate.postForObject(urlWithKey, requestBody, Map.class);

        // Response ke andar se text nikalo
        return extractTextFromResponse(response);
    }

    // Resume aur Job Description ko match krne ke liye
    public String matchResumeWithJD(String resumeText, String jobDescription) {

        String prompt = "Compare this resume with the job description.\n" +
                "Give:\n" +
                "1. Match Percentage (0-100)\n" +
                "2. Matching Skills\n" +
                "3. Missing Skills (Skill Gap)\n" +
                "4. Suggestions to improve match\n\n" +
                "Resume:\n" + resumeText + "\n\n" +
                "Job Description:\n" + jobDescription;

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();

        part.put("text", prompt);
        content.put("parts", List.of(part));
        requestBody.put("contents", List.of(content));

        String urlWithKey = apiUrl + "?key=" + apiKey;

        Map response = restTemplate.postForObject(urlWithKey, requestBody, Map.class);

        return extractTextFromResponse(response);
    }
    // Resume ke basis pe interview questions generate
    public String generateInterviewQuestions(String resumeText) {

        String prompt = "Based on this resume, generate:\n" +
                "1. 5 Technical interview questions (related to skills/projects mentioned)\n" +
                "2. 3 HR/behavioral interview questions\n" +
                "3. Suggested answers/tips for each technical question\n\n" +
                "Resume:\n" + resumeText;

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();

        part.put("text", prompt);
        content.put("parts", List.of(part));
        requestBody.put("contents", List.of(content));

        String urlWithKey = apiUrl + "?key=" + apiKey;

        for (int i = 0; i < 3; i++) {
            try {
                Map response = restTemplate.postForObject(urlWithKey, requestBody, Map.class);
                return extractTextFromResponse(response);
            } catch (Exception e) {
                if (i == 2) return "AI service is busy right now. Please try again in a minute.";
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            }
        }
        return "AI service error.";
    }

    // ================================================
// NEW METHOD 1 (Skill Gap Learning Path ke liye)
// Resume aur JD compare karke missing skills + priority + learning resources nikalta hai
// ================================================
    public String getSkillGapAnalysis(String resumeText, String jobDescription) {

        String prompt = "Compare this resume with the job description.\n" +
                "Find the missing skills and for each missing skill give:\n" +
                "1. priority (High/Medium/Low)\n" +
                "2. 2 learning resources (course name + platform)\n\n" +
                "Reply STRICTLY in this JSON format, no extra text:\n" +
                "{ \"missingSkills\": [ { \"skill\": \"Docker\", \"priority\": \"High\", " +
                "\"resources\": [\"Docker Crash Course - freeCodeCamp\", \"Docker for Beginners - Udemy\"] } ] }\n\n" +
                "Resume:\n" + resumeText + "\n\n" +
                "Job Description:\n" + jobDescription;

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();

        part.put("text", prompt);
        content.put("parts", List.of(part));
        requestBody.put("contents", List.of(content));

        String urlWithKey = apiUrl + "?key=" + apiKey;

        Map response = restTemplate.postForObject(urlWithKey, requestBody, Map.class);

        return extractTextFromResponse(response);
    }

    // ================================================
// NEW METHOD 2 (Skill Gap Learning Path ke liye)
// Gemini se mila hua JSON text ko SkillResourceDto list mein convert karta hai
// ================================================
    public List<SkillResourceDto> parseSkillGapResponse(String aiText) {
        List<SkillResourceDto> skillResourceList = new ArrayList<>();

        try {
            String cleanedText = aiText.replace("```json", "").replace("```", "").trim();

            JSONObject rootObject = new JSONObject(cleanedText);
            JSONArray skillsArray = rootObject.getJSONArray("missingSkills");

            for (int i = 0; i < skillsArray.length(); i++) {
                JSONObject skillObject = skillsArray.getJSONObject(i);

                String skill = skillObject.getString("skill");
                String priority = skillObject.getString("priority");

                List<String> resources = new ArrayList<>();
                JSONArray resourcesArray = skillObject.getJSONArray("resources");
                for (int j = 0; j < resourcesArray.length(); j++) {
                    resources.add(resourcesArray.getString(j));
                }

                skillResourceList.add(new SkillResourceDto(skill, priority, resources));
            }

        } catch (Exception e) {
            System.out.println("Skill gap parsing error: " + e.getMessage());
        }

        return skillResourceList;
    }

    // Gemini ke response se sirf text nikalta hai
    private String extractTextFromResponse(Map response) {
        try {
            List candidates = (List) response.get("candidates");
            Map firstCandidate = (Map) candidates.get(0);
            Map content = (Map) firstCandidate.get("content");
            List parts = (List) content.get("parts");
            Map firstPart = (Map) parts.get(0);
            return (String) firstPart.get("text");
        } catch (Exception e) {
            return "AI response error: " + e.getMessage();
        }
    }
}