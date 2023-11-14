package com.example.passwordtool.StudentController;

import com.example.passwordtool.Service.StudentService;
import com.example.passwordtool.StudentDto.StudentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("api/v1/student")
public class StudentController {

    @Autowired
    private StudentService studentService;


    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @PostMapping("/signup")
    public ResponseEntity<?> saveStudent(@RequestBody StudentDTO studentDTO){
        try {
            String username = studentService.addStudent(studentDTO);
            return ResponseEntity.ok().body("User created with username: " + username);
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Cannot create user: " + ex.getMessage());
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody StudentDTO studentDTO){
        boolean signInSuccess = studentService.signIn(studentDTO.getUsername(), studentDTO.getPassword());
        if (signInSuccess) {
            return ResponseEntity.ok().body("Sign-in successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }




}
