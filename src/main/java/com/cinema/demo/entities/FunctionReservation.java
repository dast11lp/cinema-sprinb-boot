package com.cinema.demo.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity(name = "function_reservation")
public class FunctionReservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_fun_res")
	private Long id;

	@Column(name = "date_res")
	private LocalDateTime dateRes;
	
	@Column (name ="total_mou_res")
	private Double totalMount;

	@JsonIgnoreProperties({ "funReservation", "hibernateLazyInitializer", "handler" })
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_use")
	private MyUser myUser;

	@JsonIgnoreProperties({ "reservationFuntions", "hibernateLazyInitializer", "handler" })
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_fun")
	private FunctionMovie functionMovie;

	@JsonIgnoreProperties({ "functionReservation", "hibernateLazyInitializer", "handler" })
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "functionReservation")
	private List<FunctionChair> functionChairs = new ArrayList<FunctionChair>();
	
    @PrePersist
    public void prePersist() {
        if (dateRes == null) {
            dateRes = LocalDateTime.now();
        }
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getDateRes() {
		return dateRes;
	}

	public void setDateRes(LocalDateTime dateRes) {
		this.dateRes = dateRes;
	}

	public Double getTotalMount() {
		return totalMount;
	}

	public void setTotalMount(Double totalMount) {
		this.totalMount = totalMount;
	}

	public MyUser getMyUser() {
		return myUser;
	}

	public void setMyUser(MyUser myUser) {
		this.myUser = myUser;
	}

	public FunctionMovie getFunctionMovie() {
		return functionMovie;
	}

	public void setFunctionMovie(FunctionMovie functionMovie) {
		this.functionMovie = functionMovie;
	}

	public List<FunctionChair> getFunctionChairs() {
		return functionChairs;
	}

	public void setFunctionChairs(List<FunctionChair> functionChairs) {
		this.functionChairs = functionChairs;
	}

}
