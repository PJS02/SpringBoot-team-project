package com.example.demo.user.controller;

import com.example.demo.user.dto.SignupRequest;
import com.example.demo.user.service.UserService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.dto.ProfileUpdateRequest;
import org.springframework.security.core.Authentication;
import com.example.demo.user.dto.PasswordChangeRequest;
import com.example.demo.user.dto.WithdrawRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.dao.DataIntegrityViolationException;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    
    public UserController(UserService userService, UserRepository userRepository) {
    	this.userService = userService;
    	this.userRepository = userRepository;
	}
    // 회원가입 화면
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signupRequest", new SignupRequest());
        return "user/signup";
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String signup(
            @Valid @ModelAttribute("signupRequest")
            SignupRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "user/signup";
        }

        try {
            userService.signup(request);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("signupError", e.getMessage());
            return "user/signup";
        }catch(DataIntegrityViolationException e) {
        	 bindingResult.reject(
        	            "signupError",
        	            "아이디 또는 이메일이 이미 사용 중입니다."
        	 );
        }

        return "user/signup";
    }

	@GetMapping("/profile")
	public String profile(
	        org.springframework.security.core.Authentication authentication,
	        Model model) {
	
	    String username = authentication.getName();
	
	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() ->
	                new IllegalArgumentException("회원을 찾을 수 없습니다.")
	            );
	
	    model.addAttribute("user", user);
	
	    return "user/profile";
	}


	// 회원정보 수정 화면
	@GetMapping("/edit")
	public String editForm(
	        Authentication authentication,
	        Model model) {

	    User user = userRepository
	            .findByUsername(authentication.getName())
	            .orElseThrow();

	    ProfileUpdateRequest request = new ProfileUpdateRequest();
	    request.setName(user.getName());
	    request.setEmail(user.getEmail());

	    model.addAttribute("profileUpdateRequest", request);

	    return "user/edit";
	}

	// 회원정보 수정 처리
	@PostMapping("/edit")
	public String edit(
	        Authentication authentication,
	        @Valid @ModelAttribute("profileUpdateRequest")
	        ProfileUpdateRequest request,
	        BindingResult bindingResult) {

	    if (bindingResult.hasErrors()) {
	        return "user/edit";
	    }

	    try {
	        userService.updateProfile(
	                authentication.getName(), request
	        );
	    } catch (IllegalArgumentException e) {
	        bindingResult.reject("editError", e.getMessage());
	        return "user/edit";
	    } catch (DataIntegrityViolationException e) {
	        bindingResult.reject(
	                "editError",
	                "이미 사용 중인 이메일이거나 저장할 수 없는 값입니다."
	        );
	        return "user/edit";
	    }


	    return "redirect:/user/profile";
	}
	

	// 비밀번호 변경 화면
	@GetMapping("/password")
	public String passwordForm(Model model) {

	    model.addAttribute(
	            "passwordChangeRequest",
	            new PasswordChangeRequest()
	    );

	    return "user/password";
	}

	// 비밀번호 변경 처리
	@PostMapping("/password")
	public String changePassword(
	        Authentication authentication,
	        @Valid @ModelAttribute("passwordChangeRequest")
	        PasswordChangeRequest request,
	        BindingResult bindingResult,
	        HttpServletRequest httpRequest,
	        HttpServletResponse httpResponse) {
	
	    if (bindingResult.hasErrors()) {
	        return "user/password";
	    }
	
	    try {
	        userService.changePassword(
	                authentication.getName(), request
	        );
	
	    } catch (IllegalArgumentException e) {
	
	        bindingResult.reject(
	                "passwordError", e.getMessage()
	        );
	
	        return "user/password";
	    }
	
	    new SecurityContextLogoutHandler().logout(
	            httpRequest,
	            httpResponse,
	            authentication
	    );
	
	    return "redirect:/login?passwordChanged";
	}



	// 회원탈퇴 화면
	@GetMapping("/withdraw")
	public String withdrawForm(Model model) {

	    model.addAttribute(
	            "withdrawRequest",
	            new WithdrawRequest()
	    );

	    return "user/withdraw";
	}

	// 회원탈퇴 처리
	@PostMapping("/withdraw")
	public String withdraw(
	        Authentication authentication,
	        @Valid @ModelAttribute("withdrawRequest")
	        WithdrawRequest request,
	        BindingResult bindingResult,
	        HttpServletRequest httpRequest,
	        HttpServletResponse httpResponse) {

	    if (bindingResult.hasErrors()) {
	        return "user/withdraw";
	    }

	    try {
	        userService.withdraw(
	                authentication.getName(), request
	        );
	    } catch (IllegalArgumentException e) {
	        bindingResult.reject(
	                "withdrawError", e.getMessage()
	        );
	        return "user/withdraw";
	    }

	    // 탈퇴 처리 후 현재 로그인 세션 종료
	    new SecurityContextLogoutHandler().logout(
	            httpRequest,
	            httpResponse,
	            authentication
	    );

	    return "redirect:/login?withdrawn";
	}


}
