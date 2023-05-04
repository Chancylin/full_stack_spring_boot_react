package com.example.demo.student;

import com.example.demo.student.exception.BadRequestException;
import com.example.demo.student.exception.StudentNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Student getStudent(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student with id " + studentId + " does not exists"));
    }

    public void addStudent(Student student) {
        // check if email is taken
        Boolean existsEmail = studentRepository.selectExistsEmail(student.getEmail());
        if (existsEmail) {
            throw new BadRequestException("Email " + student.getEmail() + " taken");
        }

        studentRepository.save(student);
    }

    public void updateStudent(Student student) {
        // check if student exists
        if (!studentRepository.existsById(student.getId())) {
            throw new StudentNotFoundException("Student with id " + student.getId() + " does not exists");
        }

        Boolean emailTakenByOther = studentRepository.selectEmailTakenByOther(student.getEmail(), student.getId());

        if (emailTakenByOther) {
            throw new BadRequestException("Email " + student.getEmail() + " taken");
        }

        studentRepository.save(student);
    }

    public void deleteStudent(Long studentId) {
        // check if student exists
        if (!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException("Student with id " + studentId + " does not exists");
        }
        studentRepository.deleteById(studentId);
    }
}
