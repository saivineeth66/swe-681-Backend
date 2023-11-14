package com.example.passwordtool.Service;


import com.example.passwordtool.Model.StudentSurvey;

import java.util.List;

public interface SurveyService {
    public StudentSurvey saveSurvey(StudentSurvey student);
    public List<StudentSurvey> getAllStudentSurvey();
}
