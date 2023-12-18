package com.challenge.challenge.controller;

import com.challenge.challenge.config.SecurityConfig;
import com.challenge.challenge.dto.LoginUserDTO;
import com.challenge.challenge.dto.RegisterUserDTO;
import com.challenge.challenge.filter.JwtUtil;
import com.challenge.challenge.model.UserEntity;
import com.challenge.challenge.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SecurityConfig securityConfig;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginUserDTO loginUserDto) {
        try {
            UserEntity currentUser=null;
            if (authService.findByEmail(loginUserDto.getEmail()).isPresent()){
                currentUser= authService.findByEmail(loginUserDto.getEmail()).get();
            }
            return ResponseEntity.ok()
                    .body(authService.generateToken(loginUserDto.getEmail(),loginUserDto.getPassword(),currentUser));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody RegisterUserDTO registerUserDto) {
        try {
            UserEntity user = new UserEntity(0L,registerUserDto.getUserName(), registerUserDto.getEmail(), registerUserDto.getPassword() , null,null,authService.setRole(registerUserDto.getRoles()),true);
            authService.save(registerUserDto);
            return ResponseEntity.ok().body(authService.generateToken(registerUserDto.getEmail(), registerUserDto.getPassword(),user));
        } catch (RuntimeException re) {
            return new ResponseEntity<>(re.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        securityConfig.invalidateToken();
        return ResponseEntity.ok("Logout successful");
    }
}
