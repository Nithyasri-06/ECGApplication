package com.ecg.ECGApplication.Service;

import com.ecg.ECGApplication.Model.ECGRecord;
import com.ecg.ECGApplication.Model.Patient;
import com.ecg.ECGApplication.Repository.ECGRepository;
import com.ecg.ECGApplication.Repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ECGRecordService {

    private final PatientRepository patientRepository;
    private final ECGRepository ecgRepository;
    @Value("${file.upload-dir}")
    private String uploadDir;
    public @Nullable Object uploadECG(Long id, MultipartFile file) throws IOException {
        Patient patient=patientRepository.findById(id).orElseThrow(()->new RuntimeException("Patient Not found"));
        Files.createDirectories(Paths.get(uploadDir));
        String fileName=System.currentTimeMillis()+"-"+file.getOriginalFilename();
        Path filePath=Paths.get(uploadDir,fileName);
        Files.write(filePath,file.getBytes());

        ECGRecord ecgRecord=new ECGRecord();
        ecgRecord.setFileName(fileName);
        ecgRecord.setFilePath(filePath.toString());
        ecgRecord.setUploadedAt(LocalDateTime.now());
        ecgRecord.setPatient(patient);

        return ecgRepository.save(ecgRecord);

    }
}
