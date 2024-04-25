package com.aadharmask.model;

import java.io.File;
import java.sql.Blob;

import javax.persistence.Lob;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AadharRequestDetailDTO {

	private String prospectId;
	private String imageName;

	@Lob
	private byte[] imageData;

//	private File imageData1;
	private String docCode;
	private String documentId;
}
