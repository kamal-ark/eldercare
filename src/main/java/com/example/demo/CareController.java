package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class CareController {
	
	@Autowired
	CareReceiverRepository careReceiverRepository;
	
	@Autowired
	CareProviderRepository careProviderRepository;
	
	@Autowired
	CareReceiverRequestRepository careReceiverRequestRepository;
	
	@Autowired
	AppointmentRepository appointmentRepository;

	// TODO Stop using Model, use REST XML/JSON
	
	// TODO add code for getting client id, manage multiple sessions
	
	// TODO create user profile pages for both care providers & receivers
	
	@GetMapping("/elder")
	public String getCareReceiverLogin() {
		return "carelogin";
	}

	@PostMapping("/careReceiver")
	public String careReceiverAdd(
			@RequestParam String careReceiverName,
			@RequestParam String location, Model model){
		CareReceiver careReceiver = new CareReceiver();
		careReceiver.setCareReceiverName(careReceiverName);
		careReceiver.setLocation(location);
		
		careReceiverRepository.save(careReceiver);
		return "redirect:/elder"; // Redirects to login page
	}
	
	@GetMapping("/provider")
	public String getCareProviderLogin() {
		return "providerlogin";
	}
	
	@PostMapping("/careProvider")
	public String serviceProviderAdd(
			@RequestParam String providerName, @RequestParam String location,
			@RequestParam String servicesOffered, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date availableDate,
			@RequestParam long avgRating,
			 Model model){
		// TODO check if service provider is present
		CareProvider careProvider = new CareProvider();
		careProvider.setProviderName(providerName);
		careProvider.setLocation(location);
		careProvider.setAvgRating(avgRating);
		careProvider.setAvailability(availableDate);
		careProvider.setServicesOffered(Arrays.asList(servicesOffered.split(",")));
		
		careProviderRepository.save(careProvider);
		
		//model.addAttribute("client", careProvider); TODO WHAT does this line do?
		return "redirect:/provider"; // Redirects to login page
	}

	@PostMapping("/elderverify")
	public String careReceiverVerify(
			@RequestParam String careReceiverUsername,
			@RequestParam String password, Model model){
		
		return "redirect:/care";
	}

	@PostMapping("/providerverify")
	public String providerVerify(
			@RequestParam String careProviderUsername,
			@RequestParam String password, Model model){
		
		return "redirect:/providerhome";
	}

	@GetMapping("/care")
	public String getCareReceiverHome() {
		return "care";
	}
	
	@GetMapping("/providerhome")
	public String getCareProviderHome() {
		return "careproviderhome";
	}
	
	@PostMapping("/addAvailability")
	public String addAvailability(@RequestParam Long clientId,
			@RequestParam Long providerId,
			@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date avStart,
			@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date avEnd,
			Model model) {
		/*
		 * RestTemplate restTemplate = new RestTemplate(); String fooResourceUrl =
		 * "http://localhost:8080/clients"; ResponseEntity<String> response =
		 * restTemplate.getForEntity(fooResourceUrl, String.class);
		 * System.out.println("Client list from server:" + response.getBody());
		 */		
		
		  LocalDateTime as = avStart.toInstant() .atZone(ZoneId.systemDefault())
		  .toLocalDateTime();
		  
		  LocalDateTime ae = avEnd.toInstant() .atZone(ZoneId.systemDefault())
		  .toLocalDateTime();
		  
		  TupleDateTime ldt = new TupleDateTime(as, ae);
		 		
		
		// Send Post
		String url = "http://localhost:8080/client/{ci}/addAvailability?providerId={pi}";
		
		/*
		 * String url = "https://localhost:8080/client/"+ clientId.toString()
		 * +"/addAvailability?providerId=" + providerId.toString();
		 * 
		 * System.out.println(url);
		 */		  RestTemplate rt = new RestTemplate();

		
//		HttpHeaders headers = new HttpHeaders();
//		HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
		
		Map<String, Long> uriVariables = new HashMap<>();
        uriVariables.put("ci", clientId);
        uriVariables.put("pi", providerId);
 
		HttpEntity<TupleDateTime> requestEntity = new HttpEntity<>(ldt);
        ResponseEntity<TupleDateTime> response = rt.exchange(url, HttpMethod.POST, requestEntity, TupleDateTime.class, uriVariables);
		
		//TupleDateTime c = rt.postForObject(url, requestEntity, TupleDateTime.class);
        
		//model.addAttribute("added", c);

		model.addAttribute("added", response.getBody());
		return "careproviderhome";
		
	}
	
	
	@PostMapping("/elderRequest")
	public String elderRequestAdd(@RequestParam Long careReceiverId,
		@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date requestDate,
		@RequestParam String serviceType, Model model){
		//  check if consumer is present
		Optional<CareReceiver> cOpt = careReceiverRepository.findById(careReceiverId);
		if(cOpt.isPresent()) {
			CareReceiver careReceiver = cOpt.get();
			// TODO fix preferred
			CareReceiverRequest careReceiverRequest = new CareReceiverRequest(requestDate, serviceType, -1);
			careReceiverRequestRepository.save(careReceiverRequest);
			careReceiver.getCareReceiverRequests().add(careReceiverRequest);
			careReceiverRepository.save(careReceiver);
		}
		
		//model.addAttribute("client", careProvider);
		return "redirect:/care/";
	}
	
	@PostMapping("/bookAppointment")
	public String appointmentAdd(@RequestParam Long careReceiverId,
			@RequestParam Long careProviderId,
			@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date requestDate,
			@RequestParam String serviceType, Model model){
			//  check if consumer is present
			Optional<CareReceiver> cOpt = careReceiverRepository.findById(careReceiverId);
			if(cOpt.isPresent()) {
				CareReceiver careReceiver = cOpt.get();
				// check if provider is present
				Optional<CareProvider> providerOpt = careProviderRepository.findById(careProviderId);
				if(providerOpt.isPresent()) {
					CareProvider provider = providerOpt.get();
					Appointment appointment = new Appointment(requestDate, serviceType, careProviderId, careReceiverId);
					appointmentRepository.save(appointment);
					
					careReceiver.getAppointments().add(appointment);
					careReceiverRepository.save(careReceiver);
					
					provider.getBookings().add(appointment);
					careProviderRepository.save(provider);
				}
			}
			
			//model.addAttribute("client", careProvider);
			return "redirect:/careproviderhome/";
		}
}
