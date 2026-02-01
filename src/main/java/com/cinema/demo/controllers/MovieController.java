package com.cinema.demo.controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.cinema.demo.entities.Movie;
import com.cinema.demo.services.MovieService;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/movies")
@CrossOrigin("*") // Simplificado
public class MovieController {

    private final MovieService movieService; // Final = Inmutabilidad

    // Inyección por constructor: Spring la detecta automáticamente sin @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<List<MovieDTO>> findAll() {
        List<Movie> movies = movieService.findAll();
        
        if (movies.isEmpty()) {
            return ResponseEntity.noContent().build(); // Devuelve 204 si no hay pelis
        }

        // Convertimos la entidad a DTO (esto se puede hacer con MapStruct o streams)
        List<MovieDTO> dtos = movies.stream()
            .map(m -> new MovieDTO(m.getId(), m.getTitle())) 
            .toList();

        return ResponseEntity.ok(dtos); // Devuelve 200 con la lista
    }
}
