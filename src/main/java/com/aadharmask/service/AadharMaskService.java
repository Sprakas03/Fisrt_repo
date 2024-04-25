package com.aadharmask.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import com.aadharmask.model.AadharMaskDetail;
import com.aadharmask.model.AadharRequestDetailDTO;
import com.aadharmask.repo.AadharMaskRepo;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Service
public class AadharMaskService {

	private final AadharMaskRepo aadharMaskRepo;

	@Value("${aadharmasking.url}")
	private String url;

	public AadharMaskService(AadharMaskRepo aadharMaskRepo) {
		this.aadharMaskRepo = aadharMaskRepo;

	}

	public void details() {

		System.out.println(aadharMaskRepo.getAadharMaskDetails());

	}

	public String aadharRequestData1() {
		try {
			List<AadharRequestDetailDTO> result = aadharMaskRepo.getAadharMaskReqDeta();

			if (result != null) {
				List<AadharMaskDetail> aadharMaskDetails = new ArrayList<>();

				int batchSize = 10;
				for (int i = 0; i < result.size(); i += batchSize) {
					List<AadharRequestDetailDTO> batch = result.subList(i, Math.min(i + batchSize, result.size()));
					List<AadharMaskDetail> batchMaskDetails = processBatch(batch);
					aadharMaskDetails.addAll(batchMaskDetails);
				}

				if (!aadharMaskDetails.isEmpty()) {
					aadharMaskRepo.insertAadharMaskDetails(aadharMaskDetails);
				}
			}

			return "Successfully stored in database";
		} catch (Exception e) {
			throw new RuntimeException("Error processing Aadhar requests: " + e.getMessage());
		}
	}

	public String aadharRequestData2() {
		try {
			List<AadharRequestDetailDTO> result = aadharMaskRepo.getAadharMaskReqDeta();

			System.out.println("db records : " + result.size());

			if (result != null) {
				int batchSize = 10;
				List<CompletableFuture<List<AadharMaskDetail>>> futures = new ArrayList<>();

				for (int i = 0; i < result.size(); i += batchSize) {
					List<AadharRequestDetailDTO> batch = result.subList(i, Math.min(i + batchSize, result.size()));
					CompletableFuture<List<AadharMaskDetail>> future = CompletableFuture
							.supplyAsync(() -> processBatch(batch));
					futures.add(future);
				}

				// Combine all CompletableFuture into a single CompletableFuture
				CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

				// Wait for all CompletableFuture to complete
				allOf.join();

				// Extract the results from CompletableFuture and accumulate
				List<AadharMaskDetail> aadharMaskDetails = new ArrayList<>();
				for (CompletableFuture<List<AadharMaskDetail>> future : futures) {
					aadharMaskDetails.addAll(future.join());
				}
				System.out.println("result size: " + aadharMaskDetails.size());

				// Insert the accumulated data into the database
				if (!aadharMaskDetails.isEmpty()) {
					aadharMaskRepo.insertAadharMaskDetails(aadharMaskDetails);
				}
			}

			return "Successfully stored in database";
		} catch (Exception e) {
			throw new RuntimeException("Error processing Aadhar requests: " + e.getMessage());
		}
	}

	private List<AadharMaskDetail> processBatch(List<AadharRequestDetailDTO> batch) {
		List<AadharMaskDetail> batchMaskDetails = new ArrayList<>();

		for (AadharRequestDetailDTO aadharRequestDetailDTO : batch) {
			try {
				byte[] fileBytes = Files.readAllBytes(Paths.get(aadharRequestDetailDTO.getImageName()));
				String filename = new File(aadharRequestDetailDTO.getImageName()).getName();

				MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//				body.add("source", "CD_IDFY");
				body.add("source", "CD_KYC_IDFY");
				body.add("imagecode", "AADHAR_F");
				body.add("refno", aadharRequestDetailDTO.getProspectId());
				body.add("file", new HttpEntity<>(fileBytes, createHeaders(filename)));

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.MULTIPART_FORM_DATA);

				HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

				RestTemplate restTemplate = new RestTemplate();
				ResponseEntity<String> responseEntity = restTemplate
						.exchange("https://intranetesignservice.tvscredit.com/esign/EsignOtp/OCR/ExtractDoc",
//						"https://intranetesignserviceuatoci.tvscredit.com/esign/EsignOtp/OCR/ExtractDoc",
								HttpMethod.POST, requestEntity, String.class);

				System.out.println("prospectid: " + aadharRequestDetailDTO.getProspectId());
				// Print the response status code and body
				System.out.println("Response status code: " + responseEntity.getStatusCode());
				System.out.println("Response body: " + responseEntity.getBody());

				if (responseEntity.getStatusCode().is2xxSuccessful()) {
					Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(), Map.class);
					if ("SR".equals(responseMap.get("status_code"))) {
						batchMaskDetails.add(AadharMaskDetail.builder()
								.prospectId(aadharRequestDetailDTO.getProspectId()).status('C')
								.accessKey((String) responseMap.get("accesskey"))
								.docCode(aadharRequestDetailDTO.getDocCode())
								.documentId(aadharRequestDetailDTO.getDocumentId()).originalImagName(filename).build());
					} else if ("ER".equals(responseMap.get("status_code"))) {
						batchMaskDetails.add(AadharMaskDetail.builder()
								.prospectId(aadharRequestDetailDTO.getProspectId()).status('E')
								.docCode(aadharRequestDetailDTO.getDocCode())
								.documentId(aadharRequestDetailDTO.getDocumentId()).originalImagName(filename).build());
					}
				}
			} catch (IOException e) {
				System.err.println("Error reading file for prospect ID " + aadharRequestDetailDTO.getProspectId() + ": "
						+ e.getMessage());
			}
		}

		return batchMaskDetails;
	}

	public static ExchangeFilterFunction errorHandler() {
		return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {

			if (clientResponse.statusCode().is5xxServerError()) {
				return clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
					System.out.println("" + "res: " + clientResponse);
//					logError(clientResponse, errorBody);
					return Mono.error(new RuntimeException(errorBody));
				});
			} else if (clientResponse.statusCode().is4xxClientError()) {
				return clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
//					logError(clientResponse, errorBody);
					return Mono.error(new RuntimeException(errorBody));
				});
			} else {
				return Mono.just(clientResponse);
			}
		});
	}

	// Helper method to create HttpHeaders for the file part
	private static HttpHeaders createHeaders(String filename) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("file", filename);
		return headers;
	}

}
