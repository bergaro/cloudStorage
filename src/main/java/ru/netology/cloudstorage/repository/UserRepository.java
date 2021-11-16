package ru.netology.cloudstorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netology.cloudstorage.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String name);
}
