package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javassist.expr.Instanceof;


import com.example.demo.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Sign in
     * 
     * @param username
     * @param password
     * @return User
     */
    public User findByUsernameAndPasswordAndDeleteAt(String username, String password, Instanceof a);
    public User findByUsernameAndDeleteAt(String username, Instanceof a);
    Boolean existsByUsername(String username);
}
