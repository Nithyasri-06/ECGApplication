package com.ecg.ECGApplication.Repository;

import com.ecg.ECGApplication.Model.ECGRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ECGRepository extends JpaRepository<ECGRecord,Long> {
    List<ECGRecord> findByPatientId(Long patientId);
}
