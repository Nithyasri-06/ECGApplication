package com.ecg.ECGApplication.dto;

import lombok.Data;

@Data
public class PatientResponse {

    private Long id;
    private String name;
    private int age;
    private String gender;

}
