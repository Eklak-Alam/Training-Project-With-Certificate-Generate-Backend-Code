package com.lic.controller;

import com.lic.dto.StudentDTO;
import com.lic.dto.UploadResponse;
import com.lic.entities.Student;
import com.lic.service.ExcelService;
import com.lic.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/students")
@RequiredArgsConstructor
public class AdminController {

    private final ExcelService excelService;
    private final StudentService studentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> uploadStudents(@RequestParam("file") MultipartFile file) {
        try {
            List<StudentDTO> studentDTOs = excelService.processFile(file);
            List<Student> savedStudents = studentService.createStudents(studentDTOs);

            List<StudentDTO> responseDTOs = savedStudents.stream()
                    .map(studentService::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    new UploadResponse(
                            "Students uploaded successfully",
                            savedStudents.size(),
                            responseDTOs
                    )
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new UploadResponse(e.getMessage(), 0, null));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(new UploadResponse("File processing error: " + e.getMessage(), 0, null));
        }
    }

    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(@RequestBody StudentDTO studentDTO) {
        Student savedStudent = studentService.createStudent(studentDTO);
        return ResponseEntity.ok(studentService.convertToDTO(savedStudent));
    }

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudentDTOs());
    }

    @GetMapping("/{pan}")
    public ResponseEntity<StudentDTO> getStudentByPan(@PathVariable String pan) {
        Student student = studentService.getStudentByPan(pan);
        return ResponseEntity.ok(studentService.convertToDTO(student));
    }
}