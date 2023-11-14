package com.example.passwordtool.StudentDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {

    @Setter
    private int studentID;

    @Setter
    private  String username;


    @Setter
    private String password;

    @Setter
    private String roles;

    @Override
    public String toString() {
        return "StudentDTO{" +
                "studentID=" + studentID +
                ", username='" + username + '\'' +
                ", Password='" + password + '\'' +
                '}';
    }
}
