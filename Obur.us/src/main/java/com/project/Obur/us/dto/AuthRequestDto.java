package com.project.Obur.us.dto; // YENİ VE DOĞRU PAKET

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDto {
    private String name;
    private String email;
    private String password;
}