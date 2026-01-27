package com.ecg.ECGApplication.Model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int age;
    private String gender;
    @Column(name = "created_At",updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
