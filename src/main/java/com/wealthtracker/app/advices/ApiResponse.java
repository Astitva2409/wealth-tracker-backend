package com.wealthtracker.app.advices;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ApiResponse<T> {
    private T data;
    private ApiError error;
    private LocalDate timestamp;

    public ApiResponse() {
        this.timestamp = LocalDate.now();
    }

    public ApiResponse(T data) {
        this();
        this.data = data;
    }

    public ApiResponse(ApiError error) {
        this();
        this.error = error;
    }
}