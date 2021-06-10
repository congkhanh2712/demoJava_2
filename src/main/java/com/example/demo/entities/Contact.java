package com.example.demo.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.demo.message.request.contact.UpdateContact;



@Entity
@Table(name = "contact")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotBlank(message = "Name required")
    @Size(min = 3, max = 50)
    private String name;

    @NotBlank(message = "Email required")
    @Size(min = 3, max = 50)
    @Email
    private String email;

    private String phone;

    private String address;

    @Column(name="delete_at")
    private Date deleteAt;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User userId;

    public Contact() {

    }

    public Contact(Contact x) {
        this.name = x.name;
        this.email = x.name;
        this.phone = x.phone;
        this.address = x.address;
        this.userId = x.userId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String mail) {
        this.email = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User getUserid() {
        return userId;
    }

    public void setUserid(User id) {
        this.userId = id;
    }

    public Date getDeleteAt(){
        return deleteAt;
    }

    public void setDeleteAt(Date date){
        this.deleteAt = date;
    }

    public Boolean updateInfo(UpdateContact x) {
        if (x.getAddress() == null && x.getName() == null && x.getEmail() == null && x.getPhone() == null) {
            return false;
        } else {
            if (x.getAddress() != null) {
                this.address = x.getAddress();
            }
            if (x.getName() != null) {
                this.name = x.getName();
            }
            if (x.getEmail() != null) {
                this.email = x.getEmail();
            }
            if (x.getPhone() != null) {
                this.phone = x.getPhone();
            }
            return true;
        }
    }
}
