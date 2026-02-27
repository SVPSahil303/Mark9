package com.sahil.Mark9.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sahil.Mark9.model.Parent;
import com.sahil.Mark9.repository.ParentRepository;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Service
public class CustomParentDetailsService implements UserDetailsService {


    private final ParentRepository parentRepository;
    
    public CustomParentDetailsService(ParentRepository parentRepository){
        this.parentRepository=parentRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Parent parent=parentRepository.findByEmail(email)
                        .orElseThrow(()->new UsernameNotFoundException("Parent Not Found!!!!!!"));
        return new User(
            parent.getEmail(),
            parent.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_PARENT"))
        );
    }
    
}
