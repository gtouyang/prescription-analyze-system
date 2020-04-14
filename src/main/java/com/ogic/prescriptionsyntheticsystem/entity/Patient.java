package com.ogic.prescriptionsyntheticsystem.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ogic
 */
@Data
public class Patient {

    /**
     * 患者ID
     */
    private int patientId;

    /**
     * 该患者下的样本
     */
    private final List<Sample> samples;

    public Patient(int patientId) {
        this.patientId = patientId;
        samples = new ArrayList<>();
    }

    public int getPatientId() {
        return patientId;
    }

    public Patient setPatientId(int patientId) {
        this.patientId = patientId;
        return this;
    }

    public List<Sample> getSamples() {
        return samples;
    }

    public Patient addSample(Sample sample) {
        samples.add(sample);
        return this;
    }
}
