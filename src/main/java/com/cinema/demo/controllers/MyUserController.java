package com.cinema.demo.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.CrossOrigin;

import com.cinema.demo.entities.MyUser;
import com.cinema.demo.services.MyUserService;


@RestController
@RequestMapping("/user/{idUser}")
@PreAuthorize("@securityService.hasUser(#idUser)")
@CrossOrigin({"*"})
public class MyUserController {
	
	@Autowired private MyUserService myUserService;
	
	
	@GetMapping("/info")
    public MyUser getUserDetails() {
		String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		System.out.println(username);
		
		if(username == null || username.isEmpty() || username.isBlank() || username == "anonymousUser") {
			return  new MyUser();
		}
		
        return myUserService.findByUser(username).orElse(null);
	}
	
	@PostMapping("/delete")
	@PreAuthorize("@securityService.hasUser(#id)")
	public String delete (@RequestBody Long id) {
		
		
		return "";
	}
	
	
	@GetMapping("/details")
	
	@Transactional(readOnly = true)
	public ResponseEntity<MyUser> findById(@PathVariable("idUser") Long idUser){
		
		MyUser user = this.myUserService.findById(idUser);
		
		return ResponseEntity.ok(user);	
	}
	
}
