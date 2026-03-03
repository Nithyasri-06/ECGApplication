package com.ecg.ECGApplication.Backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ECGRecordResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private String fileName;
    private LocalDateTime uploadedAt;

    private Object analysis;
    private List<Double> signal;


}
