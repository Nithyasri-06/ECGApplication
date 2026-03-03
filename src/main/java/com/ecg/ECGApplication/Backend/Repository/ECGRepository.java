package com.ecg.ECGApplication.Backend.Repository;

import com.ecg.ECGApplication.Backend.Model.ECGRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ECGRepository extends JpaRepository<ECGRecord,Long> {
    List<ECGRecord> findByPatientId(Long patientId);
}
