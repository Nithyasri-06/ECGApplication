package com.ecg.ECGApplication.Service;

import com.ecg.ECGApplication.dto.PatientRequest;
import com.ecg.ECGApplication.dto.PatientResponse;

import java.util.List;

public interface PatientService {

    List<PatientResponse> getAllPatients();

    PatientResponse addpatient(PatientRequest patientRequest);
}
