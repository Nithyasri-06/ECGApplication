package com.ecg.ECGApplication.Backend.Service;

import com.ecg.ECGApplication.Backend.dto.PatientRequest;
import com.ecg.ECGApplication.Backend.dto.PatientResponse;

import java.util.List;

public interface PatientService {

    List<PatientResponse> getAllPatients();

    PatientResponse addpatient(PatientRequest patientRequest);
}
