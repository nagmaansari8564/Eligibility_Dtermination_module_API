package in.ashokit.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import in.ashokit.binding.EligResponse;
import in.ashokit.service.EdServiceImpl;


@RestController
public class EdRestController {

	@Autowired
	private EdServiceImpl service;
	

	@GetMapping("/eligibility/{caseNumber}")
	public EligResponse determineEligibility(@PathVariable Long caseNumber){
	
		EligResponse eligResponse = service.determinEligibility(caseNumber);
		
		return eligResponse;
	}
	
}

