package com.example.demo;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public class CareReceiver {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long consumerId;
	private String careReceiverName;
	private String location;
	// TODO Fetch lat long from Maps API, for physical address
	// TODO add each rating given, to help in future filtering
	@ManyToMany
	private List<CareReceiverRequest> careReceiverRequests;
	@ManyToMany
	private List<Appointment> appointments;
	//@ManyToMany
	//List<Ratings>
	// pastAppointments, pastRequests TODO fields
	
	public CareReceiver() {
		super();
	}
	// Default constructors are compulsory
	
	public long getConsumerId() {
		return consumerId;
	}
	
	public void setConsumerId(long id) {
		consumerId = id;
	}
	
	public String getCareReceiverName() {
		return careReceiverName;
	}
	
	public void setCareReceiverName(String name) {
		careReceiverName = name;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public List<Appointment> getAppointments() {
		return appointments;
	}
	
	public void setAppointments(List<Appointment> appointments) {
		this.appointments = appointments;
	}
	
	public List<CareReceiverRequest> getCareReceiverRequests() {
		return careReceiverRequests;
	}
	
	public void setCareReceiverRequests(List<CareReceiverRequest> requests) {
		this.careReceiverRequests = requests;
	}
}
