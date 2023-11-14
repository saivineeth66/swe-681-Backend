package com.example.passwordtool.StudentController;

import com.example.passwordtool.Model.StudentSurvey;
import com.example.passwordtool.Service.StudentService;
import com.example.passwordtool.Service.SurveyService;
import com.example.passwordtool.StudentDto.StudentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/v1/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private SurveyService surveyService;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }


    @PostMapping("/addsurvey")
    public ResponseEntity<?> add(@RequestBody StudentSurvey studentSurvey){
        try {
            surveyService.saveSurvey(studentSurvey);
            return ResponseEntity.status(HttpStatus.CREATED).body("Survey Added Successfully");
        } catch (Exception e) {
            // Handle exception, e.g., return an error message with a different status code
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding survey");
        }
    }


    @GetMapping("/getAll")
    public ResponseEntity<List<StudentSurvey>> getAllSurvey() {
        try {
            List<StudentSurvey> surveys = surveyService.getAllStudentSurvey();
            return ResponseEntity.ok(surveys); // 200 OK with survey list in the body
        } catch (Exception e) {
            // Handle exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> saveStudent(@RequestBody StudentDTO studentDTO){
        try {
            String username = studentService.addStudent(studentDTO);
            return ResponseEntity.ok().body("User created with username: " + username);
        } catch (RuntimeException ex) {
            // In real scenarios, use @ControllerAdvice to handle exceptions globally
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
