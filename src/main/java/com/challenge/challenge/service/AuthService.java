package com.challenge.challenge.service;

import com.challenge.challenge.dto.LoginResponseDTO;
import com.challenge.challenge.dto.LoginResponseUserDTO;
import com.challenge.challenge.dto.RegisterUserDTO;
import com.challenge.challenge.exception.UserAlreadyExist;
import com.challenge.challenge.filter.JwtUtil;
import com.challenge.challenge.model.RoleEntity;
import com.challenge.challenge.model.UserEntity;
import com.challenge.challenge.repository.I_UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final I_UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;


    public void save(RegisterUserDTO user) throws RuntimeException {
        List<RoleEntity> userRoles = new ArrayList<>();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        userEntity.setRoles(setRole(user.getRoles()));
        userEntity.setStatus(true);

        Optional<UserEntity> existingUserByEmail = userRepository.findByEmail(userEntity.getEmail());
        Optional<UserEntity> existingUserByUserName = userRepository.findByUserName(userEntity.getUserName());

        if (existingUserByEmail.isPresent()) {
            throw new UserAlreadyExist("This user email already exists");
        } else if (existingUserByUserName.isPresent()) {
            throw new UserAlreadyExist("This user name already exists");
        } else {
            try {
                userRepository.save(userEntity);
            } catch (RuntimeException e) {
                throw new RuntimeException("Error saving user on the database" + e.getMessage());
            }
        }
    }

    public Optional<UserEntity> findByEmail(String email) {
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);
        return userEntity.filter(UserEntity::getStatus);
    }

    public List<RoleEntity> setRole(List<Long> roles) {
        List<RoleEntity> aux = new ArrayList<>();
        for (Long role : roles) {
            try {
                aux.add(this.roleService.getRoleById(role).get());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error adding role to the user");
            }
        }
        return aux;
    }

    private Random random = new Random();

    private long generateRandomId() {
        return random.nextLong();
    }

    public LoginResponseDTO generateToken(String email, String password, UserEntity user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );
        // Set the authentication in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String token = jwtUtil.generateAccesToken(email);

        LoginResponseUserDTO loginResponseUserDTO = new LoginResponseUserDTO(
                user.getUserName(),
                user.getRoles().stream().map(role -> String.valueOf(role.getName())).toList()
        );

        return new LoginResponseDTO(
                token,
                loginResponseUserDTO
        );
    }
}
