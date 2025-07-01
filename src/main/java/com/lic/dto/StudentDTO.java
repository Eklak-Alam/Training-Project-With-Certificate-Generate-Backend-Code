package com.lic.dto;

import lombok.Data;

@Data
public class StudentDTO {
    private String srNo;
    private String name;
    private String panNumber;
    private String licRegdNumber;
    private String branch;
    private String startDate;
    private String endDate;
    private Boolean lastUpload; // Add this field
}