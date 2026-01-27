package com.ecg.ECGApplication.dto;

import lombok.Data;

@Data
public class PatientRequest {
    private String name;
    private int age;
    private String gender;
}
