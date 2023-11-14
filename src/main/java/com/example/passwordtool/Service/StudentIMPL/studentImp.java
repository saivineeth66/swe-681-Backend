package com.example.passwordtool.Service.StudentIMPL;

import com.example.passwordtool.Model.Student;
import com.example.passwordtool.Repo.StudentRepo;
import com.example.passwordtool.Service.StudentService;
import com.example.passwordtool.StudentDto.StudentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class studentImp implements StudentService {

    @Autowired
    private StudentRepo studentRepo;

    @Override
    public String addStudent(StudentDTO studentDTO) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        Student findstudent = studentRepo.findByUsername(studentDTO.getUsername());
        if (findstudent != null) {

            throw new RuntimeException("User already exists.");
        }
        Student student = new Student();
        student.setUsername(studentDTO.getUsername());

        String encoded = bCryptPasswordEncoder.encode(studentDTO.getPassword());
        System.out.println("Encoded Password: " + encoded);
        student.setPassword(encoded);


        studentRepo.save(student);


        return student.getUsername();
    }

    @Override
    public boolean signIn(String username, String password) {
        Student student = studentRepo.findByUsername(username);
        if (student != null) {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            if (bCryptPasswordEncoder.matches(password, student.getPassword())) {
                return true; // Sign-in success
            }
        }
        return false;
    }


}
