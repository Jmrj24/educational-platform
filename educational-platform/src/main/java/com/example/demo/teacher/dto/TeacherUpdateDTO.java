package com.example.demo.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherUpdateDTO {
    private String name;
    private String mail;
    private String specialty;
}