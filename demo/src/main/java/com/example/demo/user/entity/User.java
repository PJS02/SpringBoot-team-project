package com.example.demo.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = true, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    protected User() {
    }

    public User(String username, String name,
                String email, String password) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.status = UserStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getUsername() {
    	return username;
    }

    public String getPassword() {
    	return password;
    }

    public UserStatus getStatus() {
    	return status;
    }

	public Long getUserId() {
	    return userId;
	}
	
	public String getName() {
	    return name;
	}
	
	public String getEmail() {
	    return email;
	}

	public void updateProfile(String name, String email) {
	    this.name = name;
	    this.email = email;
	    this.updatedAt = LocalDateTime.now();
	}


	public void changePassword(String encodedPassword) {
	    this.password = encodedPassword;
	    this.updatedAt = LocalDateTime.now();
	}


	public void withdraw(String encodedPassword) {
	    this.status = UserStatus.INACTIVE;
	    this.name = "탈퇴한 회원";
	    this.email = null;
	    this.password = encodedPassword;
	    this.deletedAt = LocalDateTime.now();
	    this.updatedAt = LocalDateTime.now();
	}


}
