package ru.netology.cloudstorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.netology.cloudstorage.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String name);

    @Query("select u.id from User u where u.username = :name")
    String findUserIdByUsername(@Param("name") String username);
}
