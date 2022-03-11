package com.shopme.admin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Test;

public class AmazonS3UtilTests {

	// S3 폴더 내 파일 리스트 리턴
	@Test
	public void testLIstFolder() {
		String folderName="product-images/10/";
		List<String>listKeys=AmazonS3Util.listFolder(folderName);
		listKeys.forEach(System.out::println);
	}
	
	// S3 파일 업로드
	@Test
	public void testUploadFile() throws FileNotFoundException {
		String folderName="test-upload";
		String fileName="Capture001.png";
		String filePath="C:\\" + fileName;
		
		InputStream inputStream=new FileInputStream(filePath);
		
		AmazonS3Util.uploadFile(folderName, fileName, inputStream);
	}
	
	// S3 파일 삭제
	@Test
	public void testDeleteFile() {
		String fileName="test-upload/Capture001.png";
		AmazonS3Util.deleteFile(fileName);
	}
	
	// S3 폴더 삭제
	@Test
	public void testRemoveFolder() {
		String folderName="test-upload";
		AmazonS3Util.removeFolder(folderName);
	}
	
}
