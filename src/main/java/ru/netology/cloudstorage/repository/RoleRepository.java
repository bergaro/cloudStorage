package ru.netology.cloudstorage.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.netology.cloudstorage.model.Role;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
