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
@CrossOrigin("*") 
public class MovieController {

    private final MovieService movieService; 

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<List<Movie>> findAll() {
        List<Movie> movies = movieService.findAll();
        
        if (movies.isEmpty()) {
            return ResponseEntity.noContent().build(); 
        }
        List<Movie> datos = movies.stream()
            .map(m -> new Movie())
            .toList();

        return ResponseEntity.ok(datos);
    }
}
