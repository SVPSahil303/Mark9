package com.sahil.Mark9.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sahil.Mark9.dto.ParentRegisterDto;
import com.sahil.Mark9.model.Child;
import com.sahil.Mark9.model.Parent;
import com.sahil.Mark9.repository.ParentRepository;

@Service
public class ParentService {
   
    @Autowired
    private ParentRepository parentRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Parent registerParent(ParentRegisterDto dto) {
       if(parentRepository.findByEmail(dto.getEmail()).isPresent()){
        throw new RuntimeException("Email Already registered");
       }
       if(!dto.getPassword().equals(dto.getConfirmPassword())){
        throw new RuntimeException("Passwords do not Match");
       }

       Parent parent=new Parent();
       parent.setFullName(dto.getFullName());
       parent.setEmail(dto.getEmail());
       parent.setPassword(passwordEncoder.encode(dto.getPassword()));
       parent.setPhone(dto.getPhone());
       parent.setPinHash(encodePin("1234"));

       return parentRepository.save(parent);
    }

    public String generateChildUsername(Parent parent, Child child) {
    // take first word of parent name, trim spaces
        String parentName = parent.getFullName()
                .trim()
                .split("\\s+")[0]
                .toLowerCase();

        // take first word of child name, trim spaces
        String childName = child.getChildName()
                .trim()
                .split("\\s+")[0]
                .toLowerCase();

        return parentName + "_" + childName + "_" +
                UUID.randomUUID().toString().substring(0, 4);
    }

    public String generateRandomPassword() {
        // simple random password, you can make it stronger if you want
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public boolean checkPin(String pin,Parent parent){
        if(passwordEncoder.matches(pin, parent.getPinHash())){
            return true;
        }else{
            return false;
        }
    }

    public String encodePin(String rawPin) {
        return passwordEncoder.encode(rawPin);
    }
}
