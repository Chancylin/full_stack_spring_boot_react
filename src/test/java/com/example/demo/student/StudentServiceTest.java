package com.example.demo.student;

import com.example.demo.student.exception.BadRequestException;
import com.example.demo.student.exception.StudentNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
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
        verify(studentRepository).findAll(Sort.by(Sort.Direction.ASC, "id"));
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
    void willThrowWhenEmailIsTaken() {
        // given
        String email = "jamila@gmail.com";
        Student student = new Student(
                "Jamila",
                email,
                Gender.FEMALE
        );

        given(studentRepository.selectExistsEmail(student.getEmail())) // or we can use anySting() here
                .willReturn(true);
        // when
        // then
        assertThatThrownBy(() -> underTest.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");

        verify(studentRepository, never()).save(any());
    }

    @Test
    void canDeleteStudent() {
        // given
        long studentId = 111;

        given(studentRepository.existsById(studentId)).willReturn(true);

        // when
        underTest.deleteStudent(studentId);

        // then
        // verify interaction with exact argument directly instead
        // of using argument capture
        verify(studentRepository)
                .deleteById(studentId);
//        ArgumentCaptor<Long> studentIdArgumentCaptor =
//                ArgumentCaptor.forClass(Long.class);
//
//        verify(studentRepository)
//                .deleteById(studentIdArgumentCaptor.capture()); // capture the passed value
//
//        Long capturedStudentId = studentIdArgumentCaptor.getValue();
//        assertThat(capturedStudentId).isEqualTo(studentId); // check the values are the same
    }

    @Test
    void willThrowWhenDeleteNotExistStudent() {
        // given
        long studentId = 111L;

        given(studentRepository.existsById(studentId)).willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteStudent(studentId))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + studentId + " does not exists");

        verify(studentRepository, never()).deleteById(any());
    }
}