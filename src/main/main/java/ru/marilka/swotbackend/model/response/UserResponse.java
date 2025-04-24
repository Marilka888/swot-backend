package ru.marilka.swotbackend.model.response;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


@Setter
@Getter
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private String password;
    private String token;
    private String company;
    private boolean isReg;
    private String role;
    private String fullName;


}
