package com.ticketing.dto;


public class ProjectDto {

    private Long projectId;
    private String projectName;

    public ProjectDto() {
    }

    public ProjectDto(Long projectId, String projectName) {
        this.projectId = projectId;
        this.projectName = projectName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}