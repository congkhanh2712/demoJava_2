package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.message.request.auth.ChangePassword;
import com.example.demo.message.request.auth.LoginForm;
import com.example.demo.message.request.auth.SignUpForm;
import com.example.demo.message.response.ResponseForm;
import com.example.demo.message.response.auth.LoginResponse;

import java.util.HashSet;
import java.util.Set;


import javax.servlet.http.HttpServletRequest;

import com.example.demo.entities.ERole;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import com.example.demo.repositories.RoleRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.jwt.JwtUtils;

import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    /**
     * Sign in
     *
     * @param username
     * @param password
     * @return User
     */
    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> signIn(@RequestBody LoginForm u) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(u.getUsername(), u.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        User user = userRepository.findByUsernameAndDeleteAt(username, null);
        return ResponseEntity.ok().body(new LoginResponse(true, "Đăng nhập thành công", jwt, user.getRoles()));
    }

    /**
     * Create user user.
     *
     * @param user the user
     * @return response
     */
    @PostMapping("/signup")
    public ResponseEntity<ResponseForm> create(@RequestBody SignUpForm x) {
        if (userRepository.existsByUsername(x.getUsername()).equals(true)) {
            return ResponseEntity.ok().body(new ResponseForm(false, "Error: Username is already taken! "));
        }
        // Create new user's account
        User user = new User(x);

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok().body(new ResponseForm(true, "Successful!!!"));
    }

    @PutMapping("/changepassword")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseForm> update(HttpServletRequest request, @RequestBody ChangePassword u) {
        String headerAuth = request.getHeader("Authorization");
        String jwt = "";
        if (StringUtils.hasText(headerAuth)) {
            if (headerAuth.startsWith("Bearer ")) {
                jwt = headerAuth.substring(7, headerAuth.length());
            } else {
                jwt = headerAuth;
            }
        }
        if (!jwtUtils.validateJwtToken(jwt)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        var response = new ResponseForm();
        User user = userRepository.findByUsernameAndDeleteAt(username, null);
        if (user != null) {
            BCrypt.Result result = BCrypt.verifyer().verify(u.getPassword().toCharArray(), user.getPassword());
            if (result.verified) {
                user.setPassword(u.getNewpass());
                userRepository.save(user);
                response.message = "Đổi mật khẩu thành công";
                response.succeed = true;
            } else {
                response.message = "Tên đăng nhập hoặc mật khẩu không đúng";
                response.succeed = false;
            }
        } else {
            response.message = "Tên đăng nhập hoặc mật khẩu không đúng";
            response.succeed = false;
        }
        return ResponseEntity.ok().body(response);
    }
}
