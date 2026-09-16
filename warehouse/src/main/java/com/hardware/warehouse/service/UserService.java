package com.hardware.warehouse.service;

import com.hardware.warehouse.model.User;

import java.util.List;
import java.util.Optional;

public interface UserServices {
    List<User> findAllUsers();
    Optional<User> findUserById(Long id);
    User save(User user);
    void updateUser(Long idUser, User user);
    void deleteUserById(Long idUser);
}
