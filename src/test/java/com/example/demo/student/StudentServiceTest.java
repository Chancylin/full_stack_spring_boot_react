package com.example.demo.student;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock private StudentRepository studentRepository;
    private StudentService underTest;

    @BeforeEach
    void setUp() {
        underTest = new StudentService(studentRepository);
    }

    @Test
    void canGetAllStudents() {
        // when
        underTest.getAllStudents();
        // then
        verify(studentRepository).findAll();
    }

    @Test
    void canAddStudent() {
        // given
        String email = "jamila@gmail.com";
        Student student = new Student(
                "Jamila",
                email,
                Gender.FEMALE
        );

        // when
        underTest.addStudent(student);

        // then
        ArgumentCaptor<Student> studentArgumentCaptor =
                ArgumentCaptor.forClass(Student.class);

        verify(studentRepository)
                .save(studentArgumentCaptor.capture()); // capture the passed value

        Student capturedStudent = studentArgumentCaptor.getValue();
        assertThat(capturedStudent).isEqualTo(student); // check the values are the same
    }

    @Test
    @Disabled
    void deleteStudent() {
    }
}