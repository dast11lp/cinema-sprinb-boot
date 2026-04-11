package com.cinema.demo.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.demo.entities.Card;
import com.cinema.demo.entities.FunctionChair;
import com.cinema.demo.entities.FunctionMovie;
import com.cinema.demo.entities.FunctionReservation;
import com.cinema.demo.entities.Movie;
import com.cinema.demo.entities.MyUser;
import com.cinema.demo.models.Reservation;
import com.cinema.demo.models.Ticket;
import com.cinema.demo.repositories.FunctionChairRepository;
import com.cinema.demo.repositories.FunctionReservationRepository;
import com.cinema.demo.services.CardService;
import com.cinema.demo.services.FunctionChairService;
import com.cinema.demo.services.FunctionMovieService;
import com.cinema.demo.services.FunctionReservationService;
import com.cinema.demo.services.MovieService;
import com.cinema.demo.services.MyUserService;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("reservation/user/{idUser}")
@PreAuthorize("@securityService.hasUser(#idUser)")
@CrossOrigin({ "*" })
public class FuntionReservationController {

	private static final Logger log = LoggerFactory.getLogger(FuntionReservationController.class);

	@Autowired
	private FunctionReservationService funcResSer;

	@Autowired
	private MyUserService myUserService;

	@Autowired
	private FunctionMovieService funcMovSer;

	@Autowired
	private FunctionChairService chairService;

	@Autowired
	private FunctionReservationRepository functionReservationRepository;

	@PostMapping("/reserve-function-movie")
	public ResponseEntity<?> create(@RequestBody Reservation reservation, @PathVariable Long idUser) {

		MyUser user = this.myUserService.findById(idUser);
		if (user == null)
			return ResponseEntity.ok("el usuario no se encuentra en nuestros registros");

		FunctionMovie functiMovie = this.funcMovSer.findById(reservation.getIdFunMov());
		if (functiMovie == null)
			return ResponseEntity.ok("no existe registro de la función solicitada");

		List<FunctionChair> listFunctionChair = new ArrayList<>();
		
		

		FunctionChair functionChair = null;

		for (Long idChair : reservation.getFunctionChairs()) {

			functionChair = this.chairService.findById(idChair);

			if (functionChair == null)
				return ResponseEntity.ok("una silla no se encuentra registrada");
			else if (functionChair.getAvailable() == false) {
				return ResponseEntity.ok("al menos una silla ya se encuentra ocupada");
			} else {
				listFunctionChair.add(functionChair);
			}

		}
		com.cinema.demo.entities.Function function = functiMovie.getFunction();

		FunctionReservation functionReservation = new FunctionReservation();
		functionReservation.setFunctionMovie(functiMovie);
		functionReservation.setTotalMount(function.getPriceTicket() * reservation.getFunctionChairs().size()  );
		functionReservation.setMyUser(user);

		this.funcResSer.save(functionReservation);

		for (FunctionChair chair : listFunctionChair) {
			chair.setFunctionReservation(functionReservation);
			chair.setAvailable(false);
		}
		
		functionReservation.setFunctionChairs(listFunctionChair);

		this.chairService.saveAll(listFunctionChair);

		List<Integer> listChairs = new ArrayList<>();

		listFunctionChair.forEach(chair -> listChairs.add(chair.getNumberChair()));

		
		

		Ticket ticket = new Ticket(listChairs, idUser, user.getUsername(), function.getDate(), functionReservation.getDateRes(), function.getRoom(), 0.0,
				functionReservation.getId(), functiMovie.getMovie().getMovieName(), function.getPriceTicket() * listChairs.size());

		return ResponseEntity.ok(ticket);

	}

	@GetMapping("getMyReserveIds")
	public ResponseEntity<?> getMyReserves(@PathVariable Long idUser) {
		MyUser user = this.myUserService.findById(idUser);

		if (user == null)
			return ResponseEntity.ok("el usuario no existe");

		List<FunctionReservation> funcReservations = user.getFunReservation();

		if (funcReservations.isEmpty())
			return ResponseEntity.ok("El usuario no cuenta con reservaciones");

		List<Long> reservationIds = new ArrayList<>();

		funcReservations.forEach(f -> reservationIds.add(f.getId()));

		return ResponseEntity.ok(reservationIds);
	}

	@GetMapping("getReserve/{idFuncRes}")
	public ResponseEntity<?> getMyUniqueReserve(@PathVariable Long idUser, @PathVariable Long idFuncRes) {

		MyUser user = this.myUserService.findById(idUser);
		if (user == null)
			return ResponseEntity.ok("el usuario no existe");

		List<FunctionReservation> myFunctionReservations = this.functionReservationRepository.findByMyUser(user);
		
		if (myFunctionReservations.isEmpty()) {
			return ResponseEntity.ok("El usuario no tiene reservas");
		}
		
		FunctionReservation funReservation = null;
		
		for(FunctionReservation funRes: myFunctionReservations) {
			if (funRes.getId().equals(idFuncRes)) {
				funReservation = funRes;
			}
		}
		
		List<Integer> chairs = new ArrayList<>();
		

		return ResponseEntity.ok(funReservation);
	}
	
	
	@GetMapping("getReservesPages")
	public ResponseEntity<?> getMyUniqueReservePage (@PageableDefault(page = 0, size = 1) Pageable pageable, @PathVariable Long idUser) {
		MyUser user = this.myUserService.findById(idUser);
		if (user == null)
			return ResponseEntity.ok("el usuario no existe");
		
		return ResponseEntity.ok(this.functionReservationRepository.findByMyUser(user, pageable));
	}

	@GetMapping("list")
	public ResponseEntity<?> list(@PathVariable Long idUser) {
		return ResponseEntity.ok(this.funcResSer.findById((long) 26).getFunctionChairs());
	}

}
