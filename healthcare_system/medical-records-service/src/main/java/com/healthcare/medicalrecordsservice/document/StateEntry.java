package com.healthcare.medicalrecordsservice.document;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class StateEntry<T> {
    private T state;
    private LocalDateTime timestamp;
    private Long changedByDoctorId;

    public StateEntry(T state) {
        this.state = state;
        this.timestamp = LocalDateTime.now();
    }

    public StateEntry(T state, Long changedByDoctorId) {
        this.state = state;
        this.timestamp = LocalDateTime.now();
        this.changedByDoctorId = changedByDoctorId;
    }
}
