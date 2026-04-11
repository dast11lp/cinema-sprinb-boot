package com.cinema.demo.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.CrossOrigin;

import com.cinema.demo.entities.MyUser;
import com.cinema.demo.entities.Role;
import com.cinema.demo.models.LoginCredentials;
import com.cinema.demo.security.JWTUtil;
import com.cinema.demo.services.MyUserService;
import com.cinema.demo.services.RoleService;




@RestController
@RequestMapping("/auth")
@CrossOrigin({"*"})
public class AuthController {
	
	private static final Logger log = LoggerFactory.getLogger(AuthController.class);
			
	@Autowired
	private MyUserService myUserService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired 
	private RoleService roleService;
	
	@Autowired
	private JWTUtil jwtUtil;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@PostMapping("/register")
	public ResponseEntity<?> registerHandler(@RequestBody MyUser user){

		String encodedPass = this.passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPass);

		Optional<MyUser> userDB = myUserService.findByUser(user.getUsername());

		if(userDB.isPresent()) {
			return ResponseEntity.ok("El usuario ya existe.");
		}

		user = myUserService.save(user);


		Role role = new Role();
		role.setAuthority("ROLE_USER_" + user.getIdUser());

		role.setIdUser(user.getIdUser());

		this.roleService.save(role);

		String token = this.jwtUtil.JwtGenerator(user.getUsername());

		HttpHeaders headers = new HttpHeaders();

		headers.set("Authorization", token);

		Map<String, String> tokens = new HashMap<>();
		tokens.put("Authorization", token);

		return ResponseEntity.ok().headers(headers).body(tokens);
	}
	

	
	@PostMapping("/login")
	public ResponseEntity<?> loginHandler(@RequestBody LoginCredentials body) {
	    try {
	        UsernamePasswordAuthenticationToken authInputToken = new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword());

	        MyUser user = myUserService.findByUser(body.getUsername()).orElse(null);

	        this.authenticationManager.authenticate(authInputToken); // llama internamente a MyUserDetailsService.loadUserByUsername()

	        HttpHeaders headers = new HttpHeaders();

	        String token = jwtUtil.JwtGenerator(body.getUsername());

	        Map<String, Object> userInfo = new HashMap<>();
	        userInfo.put("Authorization", token);
	        userInfo.put("userData", user);

	        headers.set("Authorization", token);

	        return ResponseEntity.ok().headers(headers).body(userInfo);
	    } catch (Exception e) {
	        log.error(e.toString());
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o Contraseña incorrectos");
	    }
	}

}

/*authenticationManager.authenticate(token)
        ↓
llama a loadUserByUsername(username)
        ↓
trae el usuario de la BD
        ↓
				passwordEncoder.matches(passwordDelRequest, passwordDeLaBD)
        ↓
				ok → sigue  |  mal → lanza excepción*/
