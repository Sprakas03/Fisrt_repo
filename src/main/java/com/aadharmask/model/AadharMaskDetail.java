package com.aadharmask.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AadharMaskDetail {
	private String prospectId;
	private Character status;
	private String accessKey;
	private String originalImagName;
	private String docCode;
	private String documentId;
}
