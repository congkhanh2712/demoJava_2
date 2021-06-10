package com.example.demo.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.StringUtils;

import com.example.demo.entities.User;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.message.request.user.UpdateUser;
import com.example.demo.message.response.ResponseForm;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.jwt.JwtUtils;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable(value = "id") Long userId) throws ResourceNotFoundException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found on:" + userId));
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/get")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<User> getUserProfile(HttpServletRequest request) throws ResourceNotFoundException {
        String headerAuth = request.getHeader("Authorization");
        String jwt = "";
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            jwt = headerAuth.substring(7, headerAuth.length());
        }
        if (!jwtUtils.validateJwtToken(jwt)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        User user = userRepository.findByUsernameAndDeleteAt(username, null)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok().body(user);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseForm> update(HttpServletRequest request, @RequestBody UpdateUser userDetails)
            throws ResourceNotFoundException {
        var response = new ResponseForm();
        String headerAuth = request.getHeader("Authorization");
        String jwt = "";
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            jwt = headerAuth.substring(7, headerAuth.length());
        }
        if (!jwtUtils.validateJwtToken(jwt)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        User user = userRepository.findByUsernameAndDeleteAt(username, null)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        response.succeed = user.updateInfo(userDetails);
        if (response.succeed.equals(true)) {
            response.message = "Update thành công";
            userRepository.save(user);
        } else {
            response.message = "Update thất bại";
        }

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseForm> delete(@PathVariable(value = "id") Long userId) throws Exception {
        var response = new ResponseForm();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found on: " + userId));

        if (user.getDeleteAt() == null) {
            user.setDeleteAt(new java.util.Date());
            userRepository.save(user);
            response.succeed = true;
            response.message = "Đã xóa user";
        } else {
            response.succeed = false;
            response.message = "User not found on: " + userId;
        }

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseForm> deleteUser(HttpServletRequest request) throws Exception {
        var response = new ResponseForm();
        String headerAuth = request.getHeader("Authorization");
        String jwt = "";
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            jwt = headerAuth.substring(7, headerAuth.length());
        }
        if (!jwtUtils.validateJwtToken(jwt)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        User user = userRepository.findByUsernameAndDeleteAt(username, null)
                .orElseThrow(() -> new ResourceNotFoundException("User not found on"));
        user.setDeleteAt(new java.util.Date());
        userRepository.save(user);
        response.succeed = true;
        response.message = "Đã xóa user";

        return ResponseEntity.ok().body(response);
    }
}
