package com.cinema.demo.controllers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.CrossOrigin;

import com.cinema.demo.entities.Card;
import com.cinema.demo.entities.MyUser;
import com.cinema.demo.services.CardService;
import com.cinema.demo.services.MyUserService;

@RestController
@RequestMapping("/card/user/{idUser}")
@PreAuthorize("@securityService.hasUser(#idUser)")
@CrossOrigin({"*"})
public class CardController {
	
	private static final Logger log = LoggerFactory.getLogger(CardController.class);
	
	@Autowired
	private CardService cardService;
	
	@Autowired
	private MyUserService myUserService;
	
	@PostMapping("/register")
	public ResponseEntity<String> create (@RequestBody Card card, @PathVariable Long idUser) {
		
		try {
			MyUser user = this.myUserService.findById(idUser);
			
			this.cardService.save(card);
			
			card.setMyUser(user);
			
			this.myUserService.save(user);
			
			return ResponseEntity.ok("Guardada");
			
		} catch (Exception e) {
			log.error(e.toString());
			return  ResponseEntity.ok(e.toString());
		}
	}
	
	
	@GetMapping("/show-cards/")
	public ResponseEntity<List<Card>> listCards(@PathVariable Long idUser){
		try {
			List<Card> cards = this.myUserService.findById(idUser).getCards();
			
			return ResponseEntity.ok(cards);
			
		} catch (Exception e) {
			log.error(e.toString());
			return ResponseEntity.ok(Arrays.asList());
		}
	}
	
	
	@PostMapping("confirm-card")
	public void confirmCard(@RequestBody Long idCard, HttpServletResponse response, PathVariable idUser) throws IOException {
		
		if (idCard == null) {
			log.error("no se insertó la un id");
			return ;
		}else {
			Card card = this.cardService.findById(idCard);
			if(card != null) {
				response.sendRedirect("reservation/user/"+ idUser +"/confirm-function/");
			}
		}
	}


	@PostMapping("delete/{idCard}")
	public String delete(@PathVariable Long idUser, @PathVariable Long idCard) {
		
		MyUser user = this.myUserService.findById(idUser);
		
		
		
		List<Card> cards = user.getCards();
		
		cards.stream().forEach(card -> System.out.println(card.getId()));
		
		Card userCard = null;
		
		
		for (Card card: cards) {
			if (card.getId().equals(idCard)) {
				userCard = card;
				break;
			}
		}
		
		this.cardService.deleteById(userCard.getId());
		
		return "impreso ";
	}
}
