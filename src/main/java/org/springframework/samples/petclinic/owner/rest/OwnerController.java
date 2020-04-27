package org.springframework.samples.petclinic.owner.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.visit.VisitRepository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * 
 * @author Awadhesh Kumar
 *
 */
@RestController("OwnerRestController")
@RequestMapping("/api/owner")
public class OwnerController {
	private final OwnerRepository owners;

	private VisitRepository visits;

	public OwnerController(OwnerRepository clinicService, VisitRepository visits) {
		this.owners = clinicService;
		this.visits = visits;
	}
	
	@PostMapping("/new")
	public ResponseEntity<Object> createOwner(@Valid @RequestBody NewOwnerForm owner, BindingResult result) {
		if (result.hasErrors()) {
			return new ResponseEntity<Object>(result.getAllErrors(),HttpStatus.BAD_REQUEST);
		} else {
			createNewOwner(owner);
			return new ResponseEntity<Object>("Owner created",HttpStatus.OK);
		}
	}
	
	@GetMapping("")
	public ResponseEntity<Object> processFindForm(NewOwnerForm owner) {

		// allow parameterless GET request to return all records
		if (owner.getLastName() == null) {
			owner.setLastName(""); // empty string signifies broadest possible search
		}

		// find owners by last name
		Collection<ExistingOwnerForm> results = getAllOwners(owner);
		if (results.isEmpty()) {
			// no owners found
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			// owners found
			return new ResponseEntity<>(results, HttpStatus.OK);
		}
	}

	private Collection<ExistingOwnerForm> getAllOwners(NewOwnerForm ownerForm) {
		Collection<Owner>existingOwners=owners.findByLastName(ownerForm.getLastName());
		Collection<ExistingOwnerForm>owners=new ArrayList<>();
		for(Owner existingOwner:existingOwners) {
			ExistingOwnerForm owner=new ExistingOwnerForm();
			owner.setId(existingOwner.getId());
			owner.setAddress(existingOwner.getAddress());
			owner.setCity(existingOwner.getCity());
			owner.setFirstName(existingOwner.getFirstName());
			owner.setLastName(existingOwner.getLastName());
			owner.setTelephone(existingOwner.getTelephone());
			owners.add(owner);
		}
		return owners;
	}

	private void createNewOwner(final NewOwnerForm owner) {
		Owner newOwner=owner.NewOwner();
		this.owners.save(newOwner);
	}
}
