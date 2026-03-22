package com.ecg.ECGApplication.Backend.Service;

import com.ecg.ECGApplication.Backend.Model.ECGRecord;
import com.ecg.ECGApplication.Backend.Model.Patient;
import com.ecg.ECGApplication.Backend.Repository.ECGRepository;
import com.ecg.ECGApplication.Backend.Repository.PatientRepository;
import com.ecg.ECGApplication.Backend.dto.ECGRecordResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ECGRecordService {

    private final PatientRepository patientRepository;
    private final ECGRepository ecgRepository;
    @Value("${file.upload-dir}")
    private String uploadDir;

    public Map<String, Object> uploadECG(Long id, MultipartFile file) throws IOException {

        Map<String, Object> response = new HashMap<>();

        try {
            // ==================== Fetch Patient ====================
            Patient patient = patientRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Patient not found"));

            // ==================== Save Uploaded File ====================
            Files.createDirectories(Paths.get(uploadDir));
            String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.write(filePath, file.getBytes());

            ECGRecord record = new ECGRecord();
            record.setFileName(fileName);
            record.setFilePath(filePath.toString());
            record.setUploadedAt(LocalDateTime.now());
            record.setPatient(patient);

            ecgRepository.save(record);

            // ==================== READ CSV INTO SIGNAL ====================
            List<Double> ecgSignal = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(filePath.toFile()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("time") || line.startsWith("Amplitude")) continue;
                    String[] parts = line.split("[,\\s]+");
                    try {
                        if (parts.length >= 2) {
                            ecgSignal.add(Double.parseDouble(parts[1]));
                        } else if (parts.length == 1) {
                            ecgSignal.add(Double.parseDouble(parts[0]));
                        }
                    } catch (NumberFormatException e) {
                        // skip invalid rows
                    }
                }
            }

            response.put("fileName", fileName);
            response.put("uploadedAt", record.getUploadedAt());
            response.put("signal", ecgSignal);
            response.put("samplingRate", 360);

            if (ecgSignal.isEmpty()) {
                response.put("analysis", Map.of("error", "No valid ECG signal found"));
                return response;
            }

            // ==================== SEND TO PYTHON API ====================
            String pythonUrl = "http://127.0.0.1:5000/analyze";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = Map.of("signal", ecgSignal);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            Map<String, Object> pythonResult;
            try {
                ResponseEntity<Map> pythonResponse = restTemplate.postForEntity(pythonUrl, requestEntity, Map.class);
                pythonResult = pythonResponse.getBody();
                if (pythonResult == null) {
                    pythonResult = Map.of("error", "Python API returned empty response");
                }
            } catch (Exception e) {
                pythonResult = Map.of("error", "Python API failed: " + e.getMessage());
            }

            ObjectMapper mapper = new ObjectMapper();
            String analysisJson = mapper.writeValueAsString(pythonResult);

            record.setAnalysisJson(analysisJson);
            ecgRepository.save(record);

            response.put("analysis", pythonResult);

        } catch (Exception e) {
            response.put("signal", Collections.emptyList());
            response.put("analysis", Map.of("error", e.getMessage()));
        }


        return response;
    }

    public List<ECGRecordResponse> getAllECGRecord(Long id) {

        List<ECGRecord> records = ecgRepository.findByPatientId(id);
        ObjectMapper mapper = new ObjectMapper();

        return records.stream().map(record -> {

            ECGRecordResponse response = new ECGRecordResponse();

            response.setId(record.getId());
            response.setPatientId(record.getPatient().getId());
            response.setPatientName(record.getPatient().getName());
            response.setFileName(record.getFileName());
            response.setUploadedAt(record.getUploadedAt());

            // -------- ONLY ANALYSIS JSON --------
            if (record.getAnalysisJson() != null && !record.getAnalysisJson().isEmpty()) {
                try {
                    Map<String, Object> json =
                            mapper.readValue(record.getAnalysisJson(), Map.class);

                    response.setAnalysis(json);   // ✔ analysis only

                } catch (Exception e) {
                    e.printStackTrace();
                    response.setAnalysis(null);
                }
            } else {
                response.setAnalysis(null);
            }


            response.setSignal(null);

            return response;

        }).collect(Collectors.toList());
    }
    public ECGRecordResponse getECGRecordById(Long id) {

        ECGRecord record = ecgRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ECG record not found"));

        ObjectMapper mapper = new ObjectMapper();
        ECGRecordResponse response = new ECGRecordResponse();

        response.setId(record.getId());
        response.setPatientId(record.getPatient().getId());
        response.setFileName(record.getFileName());
        response.setUploadedAt(record.getUploadedAt());



        try {
            Map<String, Object> json =
                    mapper.readValue(record.getAnalysisJson(), Map.class);

            response.setAnalysis(json);

            if (json.containsKey("signal")) {
                response.setSignal((List<Double>) json.get("signal"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }
}
