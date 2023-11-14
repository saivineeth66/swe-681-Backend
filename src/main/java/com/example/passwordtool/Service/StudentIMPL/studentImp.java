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
        ScryptHash scryptHash;
        Student findstudent = studentRepo.findByUsername(studentDTO.getUsername());
        if (findstudent != null) {

            throw new RuntimeException("User already exists.");
        }
        Student student = new Student();
        student.setUsername(studentDTO.getUsername());
        // Init object to get encrypted password
        scryptHash = new ScryptHash(student.getUsername(),student.getPassword());
        // Sets the salt hash value
        student.setHash(scryptHash.getStringRandomSalt());
        // gets the encrypted password
        String encoded = scryptHash.getEncryptedPassphrase().substring(0,64);
        System.out.println("Encoded Password: " + encoded);
        student.setPassword(encoded);

        studentRepo.save(student);

        return student.getUsername();
    }

    @Override
    public boolean signIn(String username, String password) {
        Student student = studentRepo.findByUsername(username);
        if (student != null) {
            ScryptHash scryptHash = new ScryptHash(username,password,student.getHash());
            if (scryptHash.getEncryptedPassphrase().substring(0,64).equals(student.getPassword())) {
                return true; // Sign-in success
            }
        }
        return false;
    }


}
