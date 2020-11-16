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
public class Course {

    private Long id;
    private String name;
    private String description;

    private List<String> tags;

}
