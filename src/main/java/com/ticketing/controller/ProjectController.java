package com.ticketing.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ticketing.domain.Project;
import com.ticketing.dto.ProjectDTO;
import com.ticketing.service.ProjectService;

@Controller
@RequestMapping("/api/admin/projects") // Consider a more appropriate base path if needed
public class ProjectController {

    @Autowired
    private ProjectService projectService;
    
    @GetMapping("/all")
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<Project> projects = projectService.findAll();
        List<ProjectDTO> projectDTOs = projects.stream()
                .map(project -> new ProjectDTO(project.getProjectId(), project.getProjectName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(projectDTOs);
    }
}