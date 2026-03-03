package com.ecg.ECGApplication.Backend.Controller;

import com.ecg.ECGApplication.Backend.Service.PatientService;
import com.ecg.ECGApplication.Backend.dto.PatientRequest;
import com.ecg.ECGApplication.Backend.dto.PatientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @GetMapping("/patients")
    public ResponseEntity<List<PatientResponse>> getAllPatients()
    {
        List<PatientResponse> patients=patientService.getAllPatients();
        return ResponseEntity.ok(patients);

    }

    @PostMapping("/patients/add")
    public ResponseEntity<PatientResponse> addPatient(@RequestBody PatientRequest patientRequest)
    {
        PatientResponse ptrespone=patientService.addpatient(patientRequest);
        return ResponseEntity.ok(ptrespone);
    }
}
