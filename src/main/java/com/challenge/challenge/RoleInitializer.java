package com.challenge.challenge;

import com.challenge.challenge.model.RoleEntity;
import com.challenge.challenge.model.RoleName;
import com.challenge.challenge.repository.I_RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements ApplicationRunner {

    private final I_RoleRepository i_roleRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (i_roleRepository.count() == 0) {
            i_roleRepository.save(new RoleEntity(1l, RoleName.USER));
            i_roleRepository.save(new RoleEntity(2l, RoleName.ADMIN));
        }
    }
}
