package com.ecg.ECGApplication.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@RequiredArgsConstructor
public class ECGRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String filePath;
    private LocalDateTime uploadedAt;
    private String prediction;

    @ManyToOne
    @JoinColumn(name = "patient_id",nullable = false)
    private Patient patient;
}
