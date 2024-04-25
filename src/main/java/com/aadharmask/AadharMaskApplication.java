package com.aadharmask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

@SpringBootApplication
public class AadharMaskApplication {

	public static void main23(String[] args) {
		SpringApplication.run(AadharMaskApplication.class, args);
	}

	public static void main2(String[] args) {
		List<String> aadharMasked = new ArrayList<>(Arrays.asList("3027CD0668192", "3028CD0265707", "3061CD0516496",
				"3054CD1279929", "3037CD0319547", "3053CD0430139", "3110CD0488465", "3029CD1089418", "3029CD1081555",
				"3015CD0072461", "3011CD0211594", "3018CD0445561", "3038CD0326727", "3058CD0919128", "3052CD0269221",
				"3041CD0346632", "3047CD0157642", "3009CD0668690", "3037CD0319165", "3037CD0310668", "3053CD0421130",
				"3037CD0320353", "3037CD0318919", "3058CD0903574", "3041CD0341719", "3065CD0160739", "3058CD0910767",
				"3058CD0919607", "3047CD0163766", "3047CD0165175", "3053CD0429715", "3074CD0325621", "3040CD0311443",
				"3053CD0429644", "3047CD0164859", "3047CD0162685", "3034CD0180137", "3043CD0127842", "3074CD0324168",
				"3058CD0919971", "3045CD0042155", "3009CD0669277", "3040CD0304083", "3033CD0181717", "3047CD0165301",
				"3047CD0161310", "3016CD0321965", "3037CD0319349", "3110CD0488765", "3040CD0310668", "3058CD0905688",
				"3058CD0920923", "3047CD0164799", "3047CD0164990", "3058CD0920729", "3058CD0903843", "3033CD0184302",
				"3045CD0036258", "3047CD0162263", "3009CD0669728", "3037CD0319148", "3025CD0597994", "3037CD0306757",
				"3053CD0429914", "3037CD0307011", "3029CD1087525", "3037CD0319581", "3053CD0420494", "3029CD1090517",
				"3054CD1279674", "3074CD0324798", "3037CD0307057", "3053CD0427406", "3018CD0450193", "3027CD0676577",
				"3051CD0411678", "3016CD0322642", "3052CD0271409", "3030CD0122167", "3037CD0319828", "3014CD0104431",
				"3027CD0675556", "3074CD0324041", "3054CD1279995", "3043CD0127580", "3024CD0405388", "3030CD0122252",
				"3016CD0323382", "3058CD0918701", "3064CD0159304", "3037CD0314180", "3018CD0450458", "3029CD1088811",
				"3052CD0271360", "3037CD0317608", "3037CD0318829", "3014CD0104589"));

		List<String> sourceData = Arrays.asList("3038CD0326727", "3058CD0919128", "3052CD0269221", "3041CD0346632",
				"3047CD0157642", "3009CD0668690", "3037CD0319165", "3037CD0310668", "3053CD0421130", "3037CD0320353",
				"3037CD0318919", "3025CD0597994", "3037CD0306757", "3053CD0429914", "3037CD0307011", "3029CD1087525",
				"3011CD0205170", "3058CD0903574", "3041CD0341719", "3065CD0160739", "3058CD0910767", "3058CD0919607",
				"3047CD0163766", "3047CD0165175", "3053CD0429715", "3037CD0319581", "3053CD0420494", "3029CD1090517",
				"3054CD1279674", "3074CD0324798", "3074CD0325621", "3040CD0311443", "3053CD0429644", "3037CD0307057",
				"3053CD0427406", "3018CD0450193", "3027CD0676577", "3051CD0411678", "3016CD0322642", "3052CD0271409",
				"3047CD0164859", "3047CD0162685", "3030CD0122167", "3037CD0319828", "3014CD0104431", "3027CD0675556",
				"3074CD0324041", "3054CD1279995", "3043CD0127580", "3034CD0180137", "3043CD0127842", "3074CD0324168",
				"3058CD0919971", "3045CD0042155", "3009CD0669277", "3024CD0405388", "3030CD0122252", "3016CD0323382",
				"3058CD0918701", "3064CD0159304", "3040CD0304083", "3033CD0181717", "3047CD0165301", "3047CD0161310",
				"3016CD0321965", "3037CD0319349", "3110CD0488765", "3037CD0314180", "3018CD0450458", "3029CD1088811",
				"3052CD0271360", "3040CD0310668", "3058CD0905688", "3058CD0920923", "3047CD0164799", "3047CD0164990",
				"3037CD0317608", "3037CD0318829", "3014CD0104589", "3027CD0668192", "3028CD0265707", "3061CD0516496",
				"3054CD1279929", "3058CD0920729", "3058CD0903843", "3033CD0184302", "3045CD0036258", "3047CD0162263",
				"3009CD0669728", "3037CD0319148", "3037CD0319547", "3053CD0430139", "3110CD0488465", "3029CD1089418",
				"3029CD1081555", "3015CD0072461", "3011CD0211594", "3018CD0445561");

		System.out.println(aadharMasked.size() + " " + sourceData.size());
		Map<String, String> result = new HashMap<>();

		for (String string : sourceData) {
			boolean matched = false;
			for (String string2 : aadharMasked) {
				if (string.equals(string2)) {
					matched = true;
					break;
				}
			}
			result.put(string, matched ? "matched" : "not matched");
		}
		ObjectMapper obj = new ObjectMapper();

		try {
			System.out.println("result: " + obj.writeValueAsString(result));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main1(String[] args) {
//		String localDirPath = "D:/latest sample project/aadharMakingImages/";
		String localDirPath = "D:/New folder/";
		String serverDirPath = "/10.222.1.212/ics_t/CD_IMAGES_2/LOS5";
		String serverHostname = "10.222.1.21";
		String username = "172.16.80.92/ADMINISTRATOR";
		String password = "csdcIMAGE2017$";

		try {
			File localDir = new File(localDirPath);
			File[] files = localDir.listFiles();
			System.out.println("files size: " + files.length);
			List<String> failedImages = new ArrayList();
			if (files != null) {
				FTPClient ftpClient = new FTPClient();
				ftpClient.connect(serverHostname);
				ftpClient.login(username, password);
				ftpClient.enterLocalPassiveMode();
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

				for (File file : files) {
					if (file.isFile()) {
						String localFilePath = file.getAbsolutePath();
						String filename = file.getName();
						FileInputStream inputStream = new FileInputStream(localFilePath);

						boolean uploaded = ftpClient.storeFile(serverDirPath + "/" + filename, inputStream);

						inputStream.close();

						if (uploaded) {
							System.out.println("File " + filename + " uploaded successfully.");
						} else {
							failedImages.add(filename);
							System.out.println("Failed to upload file " + filename);
						}
					}
				}

				ftpClient.logout();
				ftpClient.disconnect();
				System.out.println("failedImages: " + failedImages);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main89(String[] args) {
		String hostname = "10.222.1.21";
		String username = "172.16.80.92/ADMINISTRATOR";
		String password = "csdcIMAGE2017$";

		try {
			ProcessBuilder builder = new ProcessBuilder("xfreerdp", "/u:" + username, "/p:" + password,
					"/v:" + hostname);
			builder.redirectErrorStream(true);
			Process process = builder.start();

			// You can optionally wait for the process to finish
			try {
				int exitCode = process.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Print the output of the command
			/*
			 * BufferedReader reader = new BufferedReader(new
			 * InputStreamReader(process.getInputStream())); String line; while ((line =
			 * reader.readLine()) != null) { System.out.println(line); }
			 */

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws JSchException, FileNotFoundException, SftpException {
		String localDirPath = "D:/New folder/";
		String serverDirPath = "\\10.222.1.212/ics_t/CD_IMAGES_2/LOS5";
		String serverHostname = "10.222.1.21";
		String username = "172.16.80.92\\ADMINISTRATOR";
		String password = "csdcIMAGE2017$";
		int port = 22; // Default SSH port

		JSch jsch = new JSch();
		Session session = jsch.getSession(username, serverHostname, port);
		session.setPassword(password);
		session.setConfig("StrictHostKeyChecking", "no");
		session.connect();

		ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
		sftpChannel.connect();

		File localDir = new File(localDirPath);
		File[] files = localDir.listFiles();
		System.out.println("files size: " + files.length);
		for (File file : localDir.listFiles()) {
			if (file.isFile()) {
				String localFilePath = file.getAbsolutePath();
				String filename = file.getName();
				sftpChannel.put(new FileInputStream(localFilePath), serverDirPath + "/" + filename);
				System.out.println("File " + filename + " uploaded successfully.");
			}
		}

		sftpChannel.disconnect();
		session.disconnect();
//		} catch (JSchException | SftpException | FileNotFoundException ex) {
//			ex.printStackTrace();
//		}
	}

}
