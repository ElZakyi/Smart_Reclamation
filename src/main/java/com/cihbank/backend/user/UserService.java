package com.cihbank.backend.user;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    public User createUser(User user){
        user.setCreatedAt(java.time.LocalDateTime.now());
        user.setIsActive(true);
        user.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    @Transactional
    public void desactivate(Integer userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found !"));
        user.setIsActive(false);
        userRepository.save(user);
    }
    @Transactional
    public void activate(Integer userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found ! "));
        user.setIsActive(true);
        userRepository.save(user);
    }
    @Transactional
    public void updateUser(Integer id, User updatedUser){
        User user = userRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found !"));
        user.setFullName(updatedUser.getFullName());
        user.setPassword(updatedUser.getPassword());
        user.setEmail(updatedUser.getEmail());
        user.setPhone(updatedUser.getPhone());
        user.setPasswordHash(passwordEncoder.encode(updatedUser.getPassword()));
        userRepository.save(user);
    }
}
