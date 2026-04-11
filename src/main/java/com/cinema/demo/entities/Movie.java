package com.cinema.demo.entities;


import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.OneToMany;

import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonFilter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

@Entity(name="movies")
@JsonFilter("userFilter")
public class Movie {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_mov")
	private Long id;
	
	@Column(name="name_mov")
	private String movieName;
	
	@Column(name="poster_mov")
	private String poster;
	
	@Column(name="description_mov")
	private String descripction;
	
	@Column(name="country_mov")
	private String Country;
	
	@Column(name="director_mov")
	private String director;
	
	@Column(name="protagonists_mov")
	private String protagonists;
	
	@Column(name="language_mov")
	private String language;
	
	@JsonIgnoreProperties({"movie","hibernateLazyInitializer","handler"})
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "movie")
	private List<FunctionMovie> functionMovie;
	
	
	public String getDirector() {
		return director;
	}


	public void setDirector(String director) {
		this.director = director;
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	public String getCountry() {
		return Country;
	}


	public String getProtagonists() {
		return protagonists;
	}


	public void setProtagonists(String protagonists) {
		this.protagonists = protagonists;
	}


	public void setCountry(String country) {
		Country = country;
	}


	


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getMovieName() {
		return movieName;
	}


	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}


	public List<FunctionMovie> getFunctionMovie() {
		return functionMovie;
	}


	public void setFunctionMovie(List<FunctionMovie> functionMovie) {
		this.functionMovie = functionMovie;
	}


	public String getPoster() {
		return poster;
	}


	public void setPoster(String poster) {
		this.poster = poster;
	}


	public String getDescripction() {
		return descripction;
	}


	public void setDescripction(String descripction) {
		this.descripction = descripction;
	}


	
}

@Configuration
class FilterConfiguration {
    public FilterConfiguration (ObjectMapper objectMapper) {
    	SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.serializeAllExcept("functionMovie");
    	
        SimpleFilterProvider simpleFilterProvider = new SimpleFilterProvider().setFailOnUnknownId(true);
        simpleFilterProvider.addFilter("userFilter", simpleBeanPropertyFilter);

        objectMapper.setFilterProvider(simpleFilterProvider);
    }
}

