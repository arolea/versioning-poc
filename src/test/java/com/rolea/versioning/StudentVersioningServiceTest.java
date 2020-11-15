package com.rolea.versioning;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rolea.versioning.entities.Course;
import com.rolea.versioning.entities.Student;
import com.rolea.versioning.service.StudentVersioningService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class StudentVersioningServiceTest {

    @Autowired
    private StudentVersioningService studentVersioningService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void test_versioning() {
        Student studentV1 = getStudentV1();
        Student studentV2 = getStudentV2(studentV1);
        Student studentV3 = getStudentV3(studentV2);

        Student current = studentV3;

        studentVersioningService.versionStudent(studentV1, studentV2);
        studentVersioningService.versionStudent(studentV2, studentV3);

        Student versionedStudent = studentVersioningService
                .rollbackStudent(current.getVersion(), "v2", current);
        assertThat(studentV2).isEqualTo(versionedStudent);

        versionedStudent = studentVersioningService
                .rollbackStudent(current.getVersion(), "v1", current);
        assertThat(studentV1).isEqualTo(versionedStudent);
    }

    private Student getStudentV1() {
        return Student.builder().id(1L).name("Name v1").version("v1")
                .courses(new TreeSet<>(Set.of(
                        Course.builder().id(1L).name("First course").description("Description for first course v1")
                                .tags(List.of("t1", "t2", "t3")).build(),
                        Course.builder().id(2L).name("Second course").description("Description for second course v1")
                                .tags(List.of("t1", "t2")).build()
                ))).grades(Map.of(1L, 7D, 2L, 8.5D))
                .build();
    }

    @SneakyThrows
    public Student getStudentV2(Student studentV1){
        Student studentV2 = objectMapper.readValue(objectMapper.writeValueAsString(studentV1), Student.class);
        studentV2.setName("Name v2");
        studentV2.setVersion("v2");
        studentV2.getCourses().stream()
                .filter(course -> Long.valueOf(1).equals(course.getId()))
                .forEach(course -> {
                    course.setDescription("Description for first course v2");
                    course.setTags(List.of("t4", "t5"));
                });
        studentV2.getCourses().add(
                Course.builder().id(3L).name("Third course").description("Description for third course v1")
                        .tags(List.of("t1", "t2")).build()
        );
        studentV2.getGrades().put(1L, 9D);
        studentV2.getGrades().put(3L, 8D);
        return studentV2;
    }

    @SneakyThrows
    public Student getStudentV3(Student studentV2){
        Student studentV3 = objectMapper.readValue(objectMapper.writeValueAsString(studentV2), Student.class);
        studentV3.setName("Name v3");
        studentV3.setVersion("v3");
        studentV3.getGrades().put(1L, 10D);
        studentV3.getGrades().put(2L, 10D);
        studentV3.getGrades().put(3L, 10D);
        return studentV3;
    }

}
