package com.rolea.versioning.service;

import com.rolea.versioning.entities.Student;

public interface StudentVersioningService {

    void versionStudent(Student oldVersion, Student newVersion);

    Student rollbackStudent(String currentVersion, String targetVersion, Student student);

}
