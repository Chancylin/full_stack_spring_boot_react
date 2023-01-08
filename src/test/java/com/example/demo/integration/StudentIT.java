package com.example.demo.integration;

import com.example.demo.student.Gender;
import com.example.demo.student.Student;
import com.example.demo.student.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.github.javafaker.Internet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-it.properties"
)
@AutoConfigureMockMvc
public class StudentIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    private Faker faker = new Faker();

    @Test
    void canRegisterNewStudent() throws Exception {
        // given
        String name = String.format("%s %s",
                faker.name().firstName(),
                faker.name().lastName()
        );
        Student student = new Student(
                name,
                name.split(" ")[1] + "@amigoscode.edu",
                Gender.FEMALE);

        // when
        ResultActions resultActions = mockMvc
                .perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student))); // student is what we want to send
        // then
        resultActions.andExpect(status().isOk());

        List<Student> students = studentRepository.findAll();
        assertThat(students)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(student);
    }
}
