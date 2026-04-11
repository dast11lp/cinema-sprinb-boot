package com.cinema.demo.security;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinema.demo.entities.MyUser;
import com.cinema.demo.entities.Role;
import com.cinema.demo.services.MyUserService;

@Service
public class MyUserDetailsService implements UserDetailsService{
	
	
	private final static Logger log = LoggerFactory.getLogger(MyUserDetailsService.class);
	
	@Autowired 
	private MyUserService myUserService;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		
		MyUser user = this.myUserService.findByUser(username).orElse(null);
		
		log.info("this is the user: "+ username);
		
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		for (Role role: user.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
		}
		
		System.out.println("this is the length: "+authorities.size());
		
		return new User(username, user.getPassword(), authorities); //entrega a spring y hace la comparacion deh httprequest con el usuario traido de la base de datos, esto lo ahce con el bean PasswordEncoder que se configuró en SecurityConfig
	}

}
