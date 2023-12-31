package com.tictoc.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tictoc.auth.jwt.JWTAuthResponse;
import com.tictoc.dto.UserDTO;
import com.tictoc.service.UserService;
import com.tictoc.util.Validations;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/")
public class AuthController {

	@Autowired
	private AuthService authService;
	@Autowired
	private UserService userService;

	// Build Login REST API
	@PostMapping("login")
	public ResponseEntity<JWTAuthResponse> authenticate(@RequestBody AuthDTO loginDto) {

		return ResponseEntity.ok(authService.login(loginDto));
	}

	@PostMapping("register")
	public ResponseEntity<?> register(@RequestBody @Valid UserDTO user, BindingResult result, Model model) {
		UserDTO existing = userService.findByUserNameOrEmail(user.getUserName(), user.getEmail());
		if (existing != null) {
			return ResponseEntity.badRequest().body("Username or Email already exists");
		}
		if (result.hasErrors()) {
			String errorMsg = Validations.bindingError(result);
			return ResponseEntity.badRequest().body(errorMsg);
		}
		AuthDTO loginDto = new AuthDTO();
		loginDto.setUsernameOrEmail(user.getUserName());
		loginDto.setPassword(user.getPassword());
		userService.saveUser(user);
		return ResponseEntity.ok(authService.login(loginDto));
	}
}
