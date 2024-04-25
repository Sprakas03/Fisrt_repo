package com.aadharmask.repo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.imageio.ImageIO;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.aadharmask.model.AadharMaskDetail;
import com.aadharmask.model.AadharRequestDetailDTO;

@Repository
public class AadharMaskRepo {
	private final JdbcTemplate jdbcTemplate;

	public AadharMaskRepo(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void insertAadharMaskDetails(List<AadharMaskDetail> aadharMaskDetails) {
		String sql = "INSERT INTO  aadhar_mask_dtls(prospectid, status, accesskey, doc_code, document_id, original_img_name) "
				+ "VALUES (?, ?, ?, ?, ? ,?)";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
				AadharMaskDetail detail = aadharMaskDetails.get(i);
				preparedStatement.setString(1, detail.getProspectId());
				preparedStatement.setString(2, String.valueOf(detail.getStatus()));
				preparedStatement.setString(3, detail.getAccessKey());
				preparedStatement.setString(4, detail.getDocCode());
				preparedStatement.setString(5, detail.getDocumentId());

				preparedStatement.setString(6, detail.getOriginalImagName());

			}

			@Override
			public int getBatchSize() {
				return aadharMaskDetails.size();
			}
		});
	}

	public List<AadharMaskDetail> getAadharMaskDetails() {

		try {
			List<AadharMaskDetail> result = new ArrayList<>();

			String sql = "SELECT * FROM aadhar_mask_dtls";
			result = jdbcTemplate.query(sql, new RowMapper<AadharMaskDetail>() {

				@Override
				public AadharMaskDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
					AadharMaskDetail aadharMaskDetail = new AadharMaskDetail();
					aadharMaskDetail.setProspectId(rs.getString("prospectid"));
					String status = rs.getString("status");
					char statusCh = (status != null && !status.isEmpty()) ? status.charAt(0) : ' ';
					aadharMaskDetail.setStatus(statusCh);
					aadharMaskDetail.setAccessKey(rs.getString("accesskey"));

					return aadharMaskDetail;
				}

			});
			return result;

		} catch (Exception e) {
			throw new RuntimeException(e.getLocalizedMessage());

		}

	}

//	public List<AadharRequestDetailDTO> getAadharMaskReqDeta() {
//
//		try {
//			List<AadharRequestDetailDTO> result = new ArrayList<>();
//
//			String sql = "SELECT A.SZ_APPLICATION_NO,\r\n" + "       A.SZ_DOC_CODE,\r\n" + "       A.DOCUMENT_ID,\r\n"
//					+ "       A.SZ_REMARKS,\r\n" + "       B.DOCUMENT\r\n"
//					+ "  FROM T_DOCUMENTS A, DIGI_DOC_DETAILS B\r\n" + "WHERE A.DOCUMENT_ID = B.DOCUMENT_ID\r\n"
//					+ "   AND A.SZ_REMARKS LIKE '%AADHAR_CARD%'\r\n" + "   AND A.SZ_APPLICATION_NO IN ( \r\n"
//					+ "    select prospectid from TEMP_PROP1  where prospectid not in (select distinct SZ_APPLICATION_NO from temp_data) FETCH FIRST 300 ROWS ONLY)";
//			result = jdbcTemplate.query(sql, new RowMapper<AadharRequestDetailDTO>() {
//
//				@Override
//				public AadharRequestDetailDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
//					AadharRequestDetailDTO detailDTO = new AadharRequestDetailDTO();
//					String prospectId = rs.getString("SZ_APPLICATION_NO");
//					String remarks = rs.getString("SZ_REMARKS");
//
////					aadharMaskDetail.setProspectId(rs.getString("prospectid"));
//					detailDTO.setProspectId(prospectId);
//
//					detailDTO.setDocCode(rs.getString("SZ_DOC_CODE"));
//					detailDTO.setDocumentId(rs.getString("DOCUMENT_ID"));
//
//					String imgName = "D:/AadharUnMaskedImages/" + prospectId + "_"
//							+ remarks.substring(13, remarks.length()) + ".jpg";
//					System.out.println("img Nema: " + imgName);
//
//					detailDTO.setImageName(imgName);
//
//					Blob blob = rs.getBlob("DOCUMENT");
//					byte[] imageData = null;
//					if (blob != null) {
//						try {
//
//							imageData = blob.getBytes(1, (int) blob.length());
//
//							BufferedImage bufferedImage = ImageIO.read(createTempImageFile(imageData, imgName));
//
//							ImageIO.write(bufferedImage, "jpg", new File(imgName));
//
//							System.out.println("sdflkj: ");
//						} catch (Exception e) {
//							throw new RuntimeException(e.getMessage());
//						}
//					}
//
////					detailDTO.setImageData1(createTempImageFile(imageData, imgName));
//
//					return detailDTO;
//				}
//			});
//			System.out.println("result: " + result);
//
//			return result;
//
//		} catch (Exception e) {
//			throw new RuntimeException(e.getLocalizedMessage());
//
//		}
//
//	}
	public List<AadharRequestDetailDTO> getAadharMaskReqDeta() {
		CompletableFuture<List<AadharRequestDetailDTO>> future = CompletableFuture.supplyAsync(() -> {
			try {
				List<AadharRequestDetailDTO> result = new ArrayList<>();

				String sql = "SELECT A.SZ_APPLICATION_NO,\r\n" + "       A.SZ_DOC_CODE,\r\n"
						+ "       A.DOCUMENT_ID,\r\n" + "       A.SZ_REMARKS,\r\n" + "       B.DOCUMENT\r\n"
						+ "  FROM T_DOCUMENTS A, DIGI_DOC_DETAILS B\r\n" + "WHERE A.DOCUMENT_ID = B.DOCUMENT_ID\r\n"
						+ "   AND A.SZ_REMARKS LIKE '%AADHAR_CARD%'\r\n"
//						+ "   AND A.SZ_APPLICATION_NO IN ( \r\n"
//						+ "    select prospectid from TEMP_PROP1  where prospectid not in (select distinct SZ_APPLICATION_NO from temp_data) FETCH FIRST 200 ROWS ONLY)";
						+ " AND A.SZ_APPLICATION_NO IN ( '3002CD0111847' )";
				result = jdbcTemplate.query(sql, (rs, rowNum) -> {
					AadharRequestDetailDTO detailDTO = new AadharRequestDetailDTO();
					String prospectId = rs.getString("SZ_APPLICATION_NO");
					String remarks = rs.getString("SZ_REMARKS");

					detailDTO.setProspectId(prospectId);
					detailDTO.setDocCode(rs.getString("SZ_DOC_CODE"));
					detailDTO.setDocumentId(rs.getString("DOCUMENT_ID"));

					String imgName = "D:/AadharUnMaskedImages/" + prospectId + "_"
							+ remarks.substring(13, remarks.length()) + ".jpg";
					System.out.println("img Name: " + imgName);
					detailDTO.setImageName(imgName);

					Blob blob = rs.getBlob("DOCUMENT");
					byte[] imageData = null;
					if (blob != null) {
						try {
							imageData = blob.getBytes(1, (int) blob.length());
							BufferedImage bufferedImage = ImageIO.read(createTempImageFile(imageData, imgName));
							ImageIO.write(bufferedImage, "jpg", new File(imgName));
							System.out.println("Image saved");
						} catch (Exception e) {
							throw new RuntimeException(e.getMessage());
						}
					}
					return detailDTO;
				});
				System.out.println("result: " + result);
				return result;
			} catch (Exception e) {
				throw new RuntimeException(e.getLocalizedMessage());
			}
		});

		// Wait for the CompletableFuture to complete and return the result
		try {
			return future.get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<AadharRequestDetailDTO> getFailureAadharMaskReqDeta() {

		try {
			List<AadharRequestDetailDTO> result = new ArrayList<>();

//			jdbcTemplate.query(sql, new RowMapper<AadharRequestDetailDTO>() {
//
//				@Override
//				public AadharRequestDetailDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
//					AadharRequestDetailDTO detailDTO = new AadharRequestDetailDTO();
//					String prospectId = rs.getString("SZ_APPLICATION_NO");
//					String remarks = rs.getString("SZ_REMARKS")
//							
//				}};

			List<String> failureProspectid = new ArrayList<>();

			String sql = "SELECT A.SZ_APPLICATION_NO,\r\n" + "\r\n" + "       A.SZ_DOC_CODE,\r\n" + "\r\n"
					+ "       A.DOCUMENT_ID,\r\n" + "\r\n" + "       A.SZ_REMARKS,\r\n" + "\r\n"
					+ "       B.DOCUMENT\r\n" + "\r\n" + "  FROM T_DOCUMENTS A, DIGI_DOC_DETAILS B\r\n" + "\r\n"
					+ "WHERE A.DOCUMENT_ID = B.DOCUMENT_ID\r\n" + "\r\n"
					+ "   AND A.SZ_REMARKS LIKE '%AADHAR_CARD%'\r\n" + "\r\n"
//					+ "   AND A.SZ_APPLICATION_NO IN ('3064CD0164418')";
					+ "   AND A.SZ_APPLICATION_NO IN ('3000CD0619966')";
			result = jdbcTemplate.query(sql, new RowMapper<AadharRequestDetailDTO>() {

				@Override
				public AadharRequestDetailDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
					AadharRequestDetailDTO detailDTO = new AadharRequestDetailDTO();
					String prospectId = rs.getString("SZ_APPLICATION_NO");
					String remarks = rs.getString("SZ_REMARKS");

//					aadharMaskDetail.setProspectId(rs.getString("prospectid"));
					detailDTO.setProspectId(prospectId);

					String imgName = "D:/AadharUnMaskedImages/" + prospectId + "_"
							+ remarks.substring(13, remarks.length()) + ".jpg";
					System.out.println("img Nema: " + imgName);

					detailDTO.setImageName(imgName);

					Blob blob = rs.getBlob("DOCUMENT");
					byte[] imageData = null;
					if (blob != null) {
						try {

							imageData = blob.getBytes(1, (int) blob.length());

							BufferedImage bufferedImage = ImageIO.read(createTempImageFile(imageData, imgName));

							ImageIO.write(bufferedImage, "jpg", new File(imgName));

							System.out.println("sdflkj: ");
						} catch (Exception e) {
							throw new RuntimeException(e.getMessage());
						}
					}

//					detailDTO.setImageData1(createTempImageFile(imageData, imgName));

					return detailDTO;
				}
			});

			return result;

		} catch (Exception e) {
			throw new RuntimeException(e.getLocalizedMessage());

		}

	}

	private static File createTempImageFile(byte[] imageData, String imgName) {
		try {
			File tempFile = File.createTempFile(imgName, ".jpg");
			try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
				outputStream.write(imageData);
			}
			return tempFile;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
