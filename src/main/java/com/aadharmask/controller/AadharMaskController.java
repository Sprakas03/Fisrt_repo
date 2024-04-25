package com.aadharmask.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aadharmask.service.AadharMaskService;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping(path = "api/v1/")
public class AadharMaskController {

	private final AadharMaskService aMaskService;

	public AadharMaskController(AadharMaskService aMaskService) {
		this.aMaskService = aMaskService;
	}

	@GetMapping(path = "aadhar_processing")
	public ResponseEntity<Object> aadharUnMaskedImageProcessing() throws JsonProcessingException {
//		aMaskService.details();
		return ResponseEntity.ok().body(aMaskService.aadharRequestData2());
	}

	@GetMapping(path = "failuue_aadhar")
	public ResponseEntity<Object> msg() throws JsonProcessingException {
//		aMaskService.details();
		return ResponseEntity.ok().body(aMaskService.aadharRequestData1());
	}

}







