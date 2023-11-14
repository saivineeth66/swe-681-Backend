package com.example.passwordtool.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
public class StudentSurvey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private int id;

    @Getter
    @Setter
    private String firstName;

    @Getter
    @Setter
    private String lastName;

    @Getter
    @Setter
    private String streetAddress;

    @Getter
    @Setter
    private String state;

    @Getter
    @Setter
    private String city;

    @Getter
    @Setter
    private String zipcode;

    @Getter
    @Setter
    private String phoneNumber;

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private String dateOfSurvey;

    @Getter
    @Setter
    private boolean likedStudents;

    @Getter
    @Setter
    private boolean likedLocation;

    @Getter
    @Setter
    private boolean likedCampus;

    @Getter
    @Setter
    private boolean likedAtmosphere;

    @Getter
    @Setter
    private boolean likedDormRooms;

    @Getter
    @Setter
    private boolean likedSports;

    @Getter
    @Setter
    private String howInterested; // Radio buttons

    @Getter
    @Setter
    private String likelihoodRecommendation; // Dropdown list

    @Getter
    @Setter
    private String additionalComments; // Text area

    // Constructors, getters, and setters
}
