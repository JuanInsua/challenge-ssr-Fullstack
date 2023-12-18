package com.challenge.challenge.service;

import java.util.List;
import java.util.Optional;

import com.challenge.challenge.model.RoleEntity;
import com.challenge.challenge.repository.I_RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {


    private final I_RoleRepository RoleRepository;

    public List<RoleEntity> getAllRoleEntity() {
        return RoleRepository.findAll();
    }

    public Optional<RoleEntity> getRoleById(Long id) {
        return RoleRepository.findById(id);
    }

    public RoleEntity saveRole(RoleEntity role) {
        RoleEntity roleEntity = saveRole(role);

        return RoleRepository.save(roleEntity);
    }

    public RoleEntity updateRole(Long id, RoleEntity role) {
        return RoleRepository.findById(id).
                map(existingRole ->{
                    existingRole.setName(role.getName());

                    return RoleRepository.save(existingRole);
                }).orElseThrow(() -> new RuntimeException("ID Role " + id + "not found"));
    }

}
