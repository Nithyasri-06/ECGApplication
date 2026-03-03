package com.ecg.ECGApplication.Backend.Controller;

import com.ecg.ECGApplication.Backend.Model.ECGRecord;
import com.ecg.ECGApplication.Backend.Service.ECGRecordService;
import com.ecg.ECGApplication.Backend.dto.ECGRecordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor

public class ECGRecordController {

    private final ECGRecordService ecgRecordService;

    @PostMapping("/upload/{id}")
    public ResponseEntity<?> uploadECG(@PathVariable Long id, @RequestParam("file")MultipartFile file) throws Exception {
        return ResponseEntity.ok(ecgRecordService.uploadECG(id,file));
    }

    @GetMapping("/getRecords/{id}")
    public ResponseEntity<List<ECGRecordResponse>> getAllECGRecord(@PathVariable Long id) {
        return ResponseEntity.ok(ecgRecordService.getAllECGRecord(id));
    }
    @GetMapping("/getRecordById/{id}")
    public ResponseEntity<ECGRecordResponse> getRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(ecgRecordService.getECGRecordById(id));
    }


}
