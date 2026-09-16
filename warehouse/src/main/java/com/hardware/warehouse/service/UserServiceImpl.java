package com.hardware.warehouse.service;

import com.hardware.warehouse.model.User;
import com.hardware.warehouse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;
import java.util.Optional;

public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void updateUser(Long idUser, User user) {
        User userBD = findUserById(idUser).get();
        userBD.setName(user.getName());
        user.setEmail(user.getEmail());
        user.setUpdated_at(user.getUpdated_at());
        userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long idUser) {
        userRepository.deleteById(idUser);
    }
}
