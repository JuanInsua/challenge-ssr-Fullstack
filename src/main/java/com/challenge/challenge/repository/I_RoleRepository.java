package com.challenge.challenge.repository;

import com.challenge.challenge.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface I_RoleRepository extends JpaRepository<RoleEntity, Long> {

}
