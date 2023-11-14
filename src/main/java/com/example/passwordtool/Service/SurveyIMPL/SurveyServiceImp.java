package com.example.passwordtool.Service.SurveyIMPL;

import com.example.passwordtool.Model.StudentSurvey;
import com.example.passwordtool.Repo.studentRepository;
import com.example.passwordtool.Service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SurveyServiceImp implements SurveyService {

    @Autowired
    private studentRepository studentRep;

    @Override
    public StudentSurvey saveSurvey(StudentSurvey student) {
        return studentRep.save(student);
    }

    @Override
    public List<StudentSurvey> getAllStudentSurvey(){
        return studentRep.findAll();

    }

}
