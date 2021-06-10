package com.example.demo.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.demo.message.request.auth.SignUpForm;
import com.example.demo.message.request.user.UpdateUser;

import at.favre.lib.crypto.bcrypt.BCrypt;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotBlank(message = "Username required")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "Password required")
    @Size(min = 6, max = 100)
    private String password;

    @NotBlank(message = "Name required")
    @Size(min = 3, max = 50)
    private String name;

    private String address;

    private String phone;

    @Column(name="delete_at")
    private Date deleteAt;

    @ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(	name = "user_roles", 
				joinColumns = @JoinColumn(name = "user_id"), 
				inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

    public User() {

    }

    public User(User x) {
        this.username = x.getUsername();     
        this.password = x.getPassword();
        this.phone = x.getPhone();
        this.address = x.getAddress();
        this.name = x.getName();
    }

    public User(SignUpForm x) {
        this.username = x.getUsername();     
        String bcryptHashString = BCrypt.withDefaults().hashToString(12, x.getPassword().toCharArray());
        this.password = bcryptHashString;
        this.phone = x.getPhone();
        this.address = x.getAddress();
        this.name = x.getName();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        String bcryptHashString = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        this.password = bcryptHashString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getDeleteAt(){
        return deleteAt;
    }

    public void setDeleteAt(Date date){
        this.deleteAt = date;
    }

    public Boolean updateInfo(UpdateUser x) {
        if (x.getAddress() == null && x.getName() == null && x.getPhone() == null) {
            return false;
        } else {
            if (x.getAddress() != null) {
                this.address = x.getAddress();
            }
            if (x.getName() != null) {
                this.name = x.getName();
            }
            if (x.getPhone() != null) {
                this.phone = x.getPhone();
            }
            return true;
        }
    }

    public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
}
