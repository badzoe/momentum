package com.MomentumInvestments.MomentumInvestmentsApplication.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;


    @Entity
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public class Credentials {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;
        private String name;
        private String email;
        private String password;
        private String roles;
    }
