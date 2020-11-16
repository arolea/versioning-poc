package com.rolea.versioning;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rolea.versioning.entities.Course;
import com.rolea.versioning.entities.Student;
import com.rolea.versioning.service.JsonPatchService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JsonPatchServiceTest {

    @Autowired
    private JsonPatchService jsonPatchService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void test_versioning() {
        Student studentV1 = getStudentV1();
        Student studentV2 = getStudentV2(studentV1);
		Student studentV3 = getStudentV3(studentV2);

        JsonNode forwardPatchV1V2 = jsonPatchService.getDiff(studentV1, studentV2);
        JsonNode backwardPatchV2V1 = jsonPatchService.getDiff(studentV2, studentV1);

        JsonNode forwardPatchV2V3 = jsonPatchService.getDiff(studentV2, studentV3);
        JsonNode backwardPatchV3V2 = jsonPatchService.getDiff(studentV3, studentV2);

        String forwardPatchStudentJson = jsonPatchService.applyPatch(forwardPatchV1V2, studentV1).toString();
        Student forwardPatchStudent = objectMapper.readValue(forwardPatchStudentJson, Student.class);
        assertThat(studentV2).isEqualTo(forwardPatchStudent);
        forwardPatchStudentJson = jsonPatchService.applyPatch(forwardPatchV2V3, forwardPatchStudent).toString();
        forwardPatchStudent = objectMapper.readValue(forwardPatchStudentJson, Student.class);
        assertThat(studentV3).isEqualTo(forwardPatchStudent);

        String backwardPatchStudentJson = jsonPatchService.applyPatch(backwardPatchV3V2, studentV3).toString();
        Student backwardPatchStudent = objectMapper.readValue(backwardPatchStudentJson, Student.class);
        assertThat(studentV2).isEqualTo(backwardPatchStudent);
        backwardPatchStudentJson = jsonPatchService.applyPatch(backwardPatchV2V1, backwardPatchStudent).toString();
        backwardPatchStudent = objectMapper.readValue(backwardPatchStudentJson, Student.class);
        assertThat(studentV1).isEqualTo(backwardPatchStudent);
    }

    @SneakyThrows
    private Student getStudentV1() {
        Student studentV1 = Student.builder().id(1L).name("Name v1")
                .courses(Set.of(
                        Course.builder().id(1L).name("First course").description("Description for first course v1")
                                .tags(List.of("t1", "t2", "t3")).build(),
                        Course.builder().id(2L).name("Second course").description("Description for second course v1")
                                .tags(List.of("t1", "t2")).build()
                )).grades(Map.of(1L, 7D, 2L, 8.5D))
                .build();
        studentV1 = objectMapper.readValue(objectMapper.writeValueAsString(studentV1), Student.class);
        return studentV1;
    }
    
    @SneakyThrows
    public Student getStudentV2(Student studentV1){
        Student studentV2 = objectMapper.readValue(objectMapper.writeValueAsString(studentV1), Student.class);
        studentV2.setName("Name v2");
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
        studentV2 = objectMapper.readValue(objectMapper.writeValueAsString(studentV2), Student.class);
        return studentV2;
    }
    
    @SneakyThrows
    public Student getStudentV3(Student studentV2){
        Student studentV3 = objectMapper.readValue(objectMapper.writeValueAsString(studentV2), Student.class);
        studentV3.setName("Name v3");
        studentV3.getGrades().put(1L, 10D);
        studentV3.getGrades().put(2L, 10D);
        studentV3.getGrades().put(3L, 10D);
        studentV3 = objectMapper.readValue(objectMapper.writeValueAsString(studentV3), Student.class);
        return studentV3;
    }

}
