package com.example.passwordtool.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter // Lombok will generate getters for all fields
@NoArgsConstructor // Lombok will generate a no-arg constructor
@AllArgsConstructor
@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int studentID;

    @Setter
    @Column(unique = true)
    private String username; // Setter annotation only where it's needed


    @Setter
    private String password;

    @Setter
    private String hash;

}
