package com.ecg.ECGApplication.Controller;

import com.ecg.ECGApplication.Service.ECGRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ecg")
public class ECGRecordController {

    private final ECGRecordService ecgRecordService;

    @PostMapping("/upload/{id}")
    public ResponseEntity<?> uploadECG(@PathVariable Long id, @RequestParam("file")MultipartFile file) throws IOException {
        return ResponseEntity.ok(ecgRecordService.uploadECG(id,file));
    }


}
