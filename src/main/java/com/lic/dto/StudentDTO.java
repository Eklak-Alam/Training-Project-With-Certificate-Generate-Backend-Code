package com.lic.dto;

import lombok.Data;

@Data
public class StudentDTO {
    private Integer srNo;
    private String name;
    private String panNumber;
    private String licRegdNumber;
    private String branch;
    private String startDate;
    private String endDate;
}