package com.ecg.ECGApplication.Backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PatientResponse {

    private Long id;
    private String name;
    private int age;
    private String gender;
    private LocalDateTime createdAt;

}
