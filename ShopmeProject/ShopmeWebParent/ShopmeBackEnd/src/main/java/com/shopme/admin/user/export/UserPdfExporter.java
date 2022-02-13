package com.shopme.admin.user.export;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.shopme.admin.AbstractExporter;
import com.shopme.common.entity.User;

//pdf 행과 열 매핑 클래스
public class UserPdfExporter extends AbstractExporter {

	public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "application/pdf", ".pdf", "직원 목록_");
		
		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, response.getOutputStream());
		
		
		document.open();
		
		// 한글화를 위한 폰트 설정
		BaseFont baseFont = BaseFont.createFont("C:/Windows/Fonts/malgun.ttf", BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED);
		
		Font font = new Font(baseFont, 12);
		
		Paragraph paragraph = new Paragraph("직원 목록", font);
		paragraph.setAlignment(Paragraph.ALIGN_CENTER);
		
		document.add(paragraph);
		
		PdfPTable table = new PdfPTable(6);
		table.setWidthPercentage(100f);
		table.setSpacingBefore(10);
		table.setWidths(new float[] {1.2f, 3.5f, 3.0f, 3.0f, 3.0f, 1.7f});
		
		writeTableHeader(table);
		writeTableData(table, listUsers,font);
		
		document.add(table);
		
		document.close();
		
	}

	private void writeTableData(PdfPTable table, List<User> listUsers,Font font) {
		for (User user : listUsers) {
			table.addCell(String.valueOf(user.getId()));
			table.addCell(new PdfPCell(new Phrase(" " + user.getEmail(), font)));
			table.addCell(user.getLastName());
			table.addCell(user.getFirstName());
			table.addCell(new PdfPCell(new Phrase(" " + user.getRoles().toString(), font)));
			table.addCell(String.valueOf(user.isEnabled()));
			
		}
	}

	private void writeTableHeader(PdfPTable table) throws DocumentException, IOException {
		PdfPCell cell = new PdfPCell();
		
		cell.setBackgroundColor(Color.BLUE);
		cell.setPadding(5);
		
		BaseFont baseFont = BaseFont.createFont("C:/Windows/Fonts/malgun.ttf", BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED);
		Font font = new Font(baseFont, 12);
		font.setColor(Color.WHITE);		
		
		cell.setPhrase(new Phrase("ID", font));		
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("이메일", font));		
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("성", font));		
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("이름", font));		
		table.addCell(cell);		
		
		cell.setPhrase(new Phrase("권한", font));		
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("활성화", font));		
		table.addCell(cell);		
	}

}
