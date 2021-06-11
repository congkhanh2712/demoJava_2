package com.example.demo.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.StringUtils;

import com.example.demo.entities.Contact;
import com.example.demo.entities.User;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.message.request.contact.UpdateContact;
import com.example.demo.message.response.ResponseForm;
import com.example.demo.repositories.ContactRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.jwt.JwtUtils;

@RestController
@RequestMapping("/api/contact")
public class ContactController {
    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/list/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Contact> getContactListbyId(@PathVariable(value = "id") Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found on:" + userId));
        List<Contact> contact = contactRepository.findByUserIdAndDeleteAt(user, null);
        return contact;
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Contact>> getContactList(HttpServletRequest request) throws ResourceNotFoundException {
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
        User user = userRepository.findByUsernameAndDeleteAt(username, null);
        List<Contact> contact = contactRepository.findByUserIdAndDeleteAt(user, null);
        return ResponseEntity.ok().body(contact);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseForm> create(HttpServletRequest request, @RequestBody Contact x)
            throws ResourceNotFoundException {
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
        User user = userRepository.findByUsernameAndDeleteAt(username, null);
        Contact contact = contactRepository.findByEmailAndUserIdAndDeleteAt(x.getEmail(), user, null);
        if (contact == null) {
            x.setUserid(user);
            contactRepository.save(x);
            return ResponseEntity.ok().body(new ResponseForm(true, "Thêm liên hệ thành công"));
        }
        return ResponseEntity.ok().body(new ResponseForm(false, "Email người liên hệ đã tồn tại"));
    }

    /**
     * Update a contact
     *
     * @param userId
     * @return response
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseForm> update(HttpServletRequest request, @PathVariable(value = "id") Long contactId,
            @RequestBody UpdateContact x) throws Exception {
        var response = new ResponseForm();
        String headerAuth = request.getHeader("Authorization");
        String jwt = "";
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found on:" + contactId));
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
        if (!username.equals(contact.getUserid().getUsername())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        response.succeed = contact.updateInfo(x);
        if (response.succeed.equals(true)) {
            response.message = "Update thành công";
            contactRepository.save(contact);
        } else {
            response.message = "Update thất bại";
        }
        return ResponseEntity.ok().body(response);
    }

    /**
     * Delete a contact
     *
     * @param userId
     * @return response
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseForm> delete(HttpServletRequest request, @PathVariable(value = "id") Long contactId)
            throws Exception {
        var response = new ResponseForm();
        String headerAuth = request.getHeader("Authorization");
        String jwt = "";
        Contact contact = contactRepository.findByIdAndDeleteAt(contactId, null)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found on: " + contactId));
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
        if (!username.equals(contact.getUserid().getUsername())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        contact.setDeleteAt(new java.util.Date());
        contactRepository.save(contact);
        response.succeed = true;
        response.message = "Đã xóa liên hệ";
        return ResponseEntity.ok().body(response);
    }
}
