package com.ticketing.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ticketing.domain.Department;
import com.ticketing.dto.DepartmentDTO;
import com.ticketing.service.DepartmentService;

@Controller
@RequestMapping("/api/admin/departments") // Consider a more appropriate base path if needed
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;  
    
    @GetMapping("/all")
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        List<Department> departments = departmentService.findAll();
        List<DepartmentDTO> departmentDTOs = departments.stream()
                .map(department -> new DepartmentDTO(department.getDepartmentId(), department.getDepartmentName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(departmentDTOs);
    }
    
    
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Department> getDepartmentById(@PathVariable Long id) {
        return departmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}