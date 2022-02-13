package com.shopme.admin.user.export;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.admin.AbstractExporter;
import com.shopme.common.entity.User;

// csv 헤더와 필드 매핑 클래스
public class UserCsvExporter extends AbstractExporter {
	
	public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "text/csv", ".csv", "직원 목록_"); // csv 파일 이름 설정
		
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), 
				CsvPreference.STANDARD_PREFERENCE);
		
		// 헤더와 필드 값 매핑
		String[] csvHeader = {"직원 ID", "이메일","성", "이름",  "권한", "활성화"};
		String[] fieldMapping = {"id", "email", "lastName", "firstName", "roles", "enabled"};
		
		csvWriter.writeHeader(csvHeader);
		
		for (User user : listUsers) {
			csvWriter.write(user, fieldMapping);
		}
		
		csvWriter.close();
	}
}
