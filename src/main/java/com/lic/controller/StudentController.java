package com.lic.controller;

import com.lic.dto.StudentDTO;
import com.lic.entities.Student;
import com.lic.repository.StudentRepository;
import com.lic.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000/", allowCredentials = "true")
public class StudentController {

    private final StudentService studentService;
    private final StudentRepository studentRepository;

    @PostMapping("/details")
    public ResponseEntity<StudentDTO> getStudentByPan(@RequestBody PanRequest request) {
        Student student = studentService.getStudentByPan(request.getPanNumber());
        return ResponseEntity.ok(studentService.convertToDTO(student));
    }

    public static class PanRequest {
        private String panNumber;

        // Getters and setters
        public String getPanNumber() {
            return panNumber;
        }

        public void setPanNumber(String panNumber) {
            this.panNumber = panNumber;
        }
    }

    @GetMapping("/last-uploaded")
    public ResponseEntity<List<StudentDTO>> getLastUploadedStudents() {
        List<Student> students = studentRepository.findLastUploadedStudents();
        List<StudentDTO> dtos = students.stream()
                .map(studentService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}