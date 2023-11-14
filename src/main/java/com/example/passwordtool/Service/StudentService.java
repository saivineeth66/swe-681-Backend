package com.example.passwordtool.Service;

import com.example.passwordtool.StudentDto.StudentDTO;

public interface StudentService {

    boolean signIn(String username, String password);

    public String addStudent(StudentDTO studentDTO);
}
