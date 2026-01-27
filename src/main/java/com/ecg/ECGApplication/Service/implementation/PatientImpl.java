package com.ecg.ECGApplication.Service.implementation;

import com.ecg.ECGApplication.Model.Patient;
import com.ecg.ECGApplication.Repository.PatientRepository;
import com.ecg.ECGApplication.Service.PatientService;
import com.ecg.ECGApplication.dto.PatientRequest;
import com.ecg.ECGApplication.dto.PatientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientImpl implements PatientService {

    private final PatientRepository patientRepository;
    @Override
    public List<PatientResponse> getAllPatients() {
        return patientRepository.findAll().stream().map(p->{
            PatientResponse res=new PatientResponse();
            res.setId(p.getId());
            res.setName(p.getName());
            res.setAge(p.getAge());
            res.setGender(p.getGender());
            return res;
        }).collect(Collectors.toList());
    }

    @Override
    public PatientResponse addpatient(PatientRequest patientRequest) {
        //to get patient(from patientrequest dto) because in repo we can save only the entity
        Patient patient=new Patient();
        patient.setName(patientRequest.getName());
        patient.setAge(patientRequest.getAge());
        patient.setGender(patientRequest.getGender());
        //saved in db
        Patient saved=patientRepository.save(patient);
        //to return respone(dto)

        PatientResponse response=new PatientResponse();
        response.setId(saved.getId());
        response.setName(saved.getName());
        response.setAge(saved.getAge());
        response.setGender(saved.getGender());

        return response;
    }
}
