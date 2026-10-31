package com.example.demo.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.user.dto.SignupRequest;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.dto.ProfileUpdateRequest;
import com.example.demo.user.dto.PasswordChangeRequest;
import java.util.UUID;
import com.example.demo.user.dto.WithdrawRequest;
import com.example.demo.user.entity.UserStatus;
import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


	@Transactional
	public void signup(SignupRequest request) {
	
	    String username = request.getUsername().trim();
	    String name = request.getName().trim();
	    String email = request.getEmail()
	            .trim()
	            .toLowerCase(Locale.ROOT);
	
	    // 공백 제거 후에도 이름이 있는지 확인
	    if (name.isBlank()) {
	        throw new IllegalArgumentException(
	                "이름을 입력해주세요."
	        );
	    }
	
	    // 아이디 중복 확인
	    if (userRepository.existsByUsername(username)) {
	        throw new IllegalArgumentException(
	                "이미 사용 중인 아이디입니다."
	        );
	    }
	
	    // 이메일 중복 확인
	    if (userRepository.existsByEmail(email)) {
	        throw new IllegalArgumentException(
	                "이미 사용 중인 이메일입니다."
	        );
	    }
	
	    // 비밀번호 일치 확인
	    if (!request.getPassword().equals(
	            request.getPasswordConfirm())) {
	        throw new IllegalArgumentException(
	                "비밀번호가 일치하지 않습니다."
	        );
	    }
	
	    String encodedPassword =
	            passwordEncoder.encode(request.getPassword());
	
	    User user = new User(
	            username,
	            name,
	            email,
	            encodedPassword
	    );
	
	    userRepository.saveAndFlush(user);
	}


	@Transactional
	public void updateProfile(
	        String username,
	        ProfileUpdateRequest request) {
	
	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() ->
	                new IllegalArgumentException("회원을 찾을 수 없습니다.")
	            );
	   
	    String newName = request.getName().trim();
	    String newEmail = request.getEmail()
	            .trim()
	            .toLowerCase(Locale.ROOT);
	    
	    if (newName.isBlank()) {
	        throw new IllegalArgumentException(
	                "이름을 입력해주세요."
	        );
	    }
	    
	    // 다른 회원이 사용 중인 이메일인지 확인
	    if (!user.getEmail().equals(newEmail)
	            && userRepository.existsByEmail(newEmail)) {
	        throw new IllegalArgumentException(
	                "이미 사용 중인 이메일입니다."
	        );
	    }
	
	    // 회원정보 수정
	    user.updateProfile(newName, newEmail);
	}

	@Transactional
	public void changePassword(
	        String username,
	        PasswordChangeRequest request) {
	
	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() ->
	                new IllegalArgumentException("회원을 찾을 수 없습니다.")
	            );
	
	    // 현재 비밀번호 확인
	    if (!passwordEncoder.matches(
	            request.getCurrentPassword(),
	            user.getPassword())) {
	
	        throw new IllegalArgumentException(
	                "현재 비밀번호가 올바르지 않습니다."
	        );
	    }
	
	    // 새 비밀번호 일치 확인
	    if (!request.getNewPassword()
	            .equals(request.getConfirmPassword())) {
	
	        throw new IllegalArgumentException(
	                "새 비밀번호가 일치하지 않습니다."
	        );
	    }
	
	    // 기존 비밀번호와 동일한지 확인
	    if (passwordEncoder.matches(
	            request.getNewPassword(),
	            user.getPassword())) {
	
	        throw new IllegalArgumentException(
	                "기존 비밀번호와 다른 비밀번호를 입력해주세요."
	        );
	    }
	
	    // BCrypt 암호화 후 저장
	    String encodedPassword =
	            passwordEncoder.encode(request.getNewPassword());
	
	    user.changePassword(encodedPassword);
	}

	@Transactional
	public void withdraw(
	        String username,
	        WithdrawRequest request) {
	
	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() ->
	                new IllegalArgumentException("회원을 찾을 수 없습니다.")
	            );
	
	    if (user.getStatus() != UserStatus.ACTIVE) {
	        throw new IllegalArgumentException(
	                "이미 탈퇴한 회원입니다."
	        );
	    }
	
	    // 현재 비밀번호 확인
	    if (!passwordEncoder.matches(
	            request.getPassword(),
	            user.getPassword())) {
	
	        throw new IllegalArgumentException(
	                "비밀번호가 올바르지 않습니다."
	        );
	    }
	
	    // 탈퇴 후 기존 비밀번호로 인증되지 않도록 변경
	    String unusablePassword = passwordEncoder.encode(
	            UUID.randomUUID().toString()
	    );
	
	    // 계정 비활성화 및 개인정보 처리
	    user.withdraw(unusablePassword);
	}


	
}
