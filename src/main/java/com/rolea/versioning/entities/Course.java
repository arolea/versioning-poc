package com.rolea.versioning.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course implements Comparable<Course>{

    private Long id;
    private String name;
    private String description;

    private List<String> tags;

    @Override
    public int compareTo(Course course) {
        return (int)(this.id - course.getId());
    }

}
