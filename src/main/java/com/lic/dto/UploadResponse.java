package com.lic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UploadResponse {
    private String message;
    private int count;
    private List<StudentDTO> students;
}