package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javassist.expr.Instanceof;

import java.util.List;
import java.util.Optional;

import com.example.demo.entities.Contact;
import com.example.demo.entities.User;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    public List<Contact> findByUserIdAndDeleteAt(User id, Instanceof a);
    public Contact findByEmailAndUserIdAndDeleteAt(String email, User id, Instanceof a);
    public Optional<Contact> findByIdAndDeleteAt(Long id, Instanceof a);
}
