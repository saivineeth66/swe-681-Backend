package com.example.passwordtool.Repo;

import com.example.passwordtool.Model.StudentSurvey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface studentRepository extends JpaRepository<StudentSurvey, Integer> {


}
