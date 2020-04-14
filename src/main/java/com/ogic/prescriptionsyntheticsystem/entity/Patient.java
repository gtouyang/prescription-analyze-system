package com.ogic.prescriptionsyntheticsystem.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Patient {

    private int patientId;

    private List<Sample> samples;

    public Patient(int patientId) {
        this.patientId = patientId;
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
        if (samples == null){
            samples = new ArrayList<>();
        }
        samples.add(sample);
        return this;
    }
}
