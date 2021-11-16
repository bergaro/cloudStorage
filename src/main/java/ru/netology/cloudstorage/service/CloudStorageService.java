package ru.netology.cloudstorage.service;


import ru.netology.cloudstorage.model.User;

import java.util.List;


public interface CloudStorageService {

    User register(User user);

    List<User> getAll();

    User findByUsername(String username);

    User findById(Long id);

    void delete(Long id);
}
