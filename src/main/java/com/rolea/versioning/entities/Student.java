package com.rolea.versioning.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Student implements Comparable<Student> {

    private Long id;
    private String name;

    private Set<Course> courses;
    private Map<Long, Double> grades;

    private String version;

    @Override
    public int compareTo(Student student) {
        return (int)(this.id - student.getId());
    }

}
