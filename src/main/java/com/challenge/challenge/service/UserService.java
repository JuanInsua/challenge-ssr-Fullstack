package com.challenge.challenge.service;

import com.challenge.challenge.dto.RegisterUserDTO;
import com.challenge.challenge.model.UserEntity;
import com.challenge.challenge.repository.I_RoleRepository;
import com.challenge.challenge.repository.I_UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService  {

    private final PasswordEncoder passwordEncoder;

    private final I_UserRepository userRepository;

    private final I_RoleRepository roleRepository;

    //Recupera un usuario por su ID
    public Optional<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }

    //Update de usuario existente
    public UserEntity updateUser(Long id, UserEntity newUser) {
        try {
            UserEntity us = getUserById(newUser.getId()).get();
            return userRepository.findById(id)
                    .map(existingUser -> {
                        existingUser.setUserName(newUser.getUserName());
                        existingUser.setEmail(newUser.getEmail());
                        existingUser.setPassword(newUser.getPassword() == null || newUser.getPassword().isBlank()? us.getPassword() : passwordEncoder.encode(newUser.getPassword()));
                        existingUser.setRoles(newUser.getRoles());
                        existingUser.setStatus(newUser.getStatus());
                        return userRepository.save(existingUser);
                    })
                    .orElseThrow(() -> new RuntimeException("Id User " + id + " not found"));
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Error updating user with id " + id);
        }
    }
    //Borra un usuario
    public void deleteUser(Long id) {
        try {
            userRepository.findById(id)
                    .map(existingUser -> {
                        existingUser.setStatus(false);
                        return userRepository.save(existingUser);
                    })
                    .orElseThrow(() -> new RuntimeException("Id user \" + id + \" not found"));
        }catch (Exception e){
            throw new RuntimeException("Error deleting user with id " + id);
        }
    }

    public UserEntity changeRole(UserEntity user){
        try {
            UserEntity userSave=null;
            if (userRepository.findById(user.getId()).isPresent()) {
                userSave=userRepository.findById(user.getId()).get();
                if (userSave.getRoles().get(0).getName().name().equalsIgnoreCase("ADMIN")) {
                    userSave.getRoles().set(0,roleRepository.findById(1L).get());
                } else {
                    userSave.getRoles().set(0,roleRepository.findById(2L).get());
                }
            }else {
                throw new RuntimeException("User not found");
            }
            return userRepository.save(userSave);
        }catch (Exception e) {
            throw new RuntimeException("Error updating user with id " + user.getId());
        }
    }

    public Integer countUsers() {
        try {
            // Use Optional to handle the possibility of null value
            Optional<Integer> countOptional = Optional.ofNullable(userRepository.countUsers());

            return countOptional.orElseThrow(() -> new RuntimeException("Count of users is null"));

        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException("Failed to count users. Reason: " + e.getMessage());
        }
    }
}
