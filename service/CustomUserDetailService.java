package com.hma.hotel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hma.hotel.exception.OurException;
import com.hma.hotel.repo.UserRepo;


@Service
public class CustomUserDetailService implements UserDetailsService{

@Autowired
private UserRepo userRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      
        return userRepo.findByEmail(username).orElseThrow(()-> new OurException("User Not Found!!!"));
    }

}