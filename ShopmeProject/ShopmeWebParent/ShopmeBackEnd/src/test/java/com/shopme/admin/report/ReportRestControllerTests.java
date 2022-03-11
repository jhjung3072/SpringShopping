package com.shopme.admin.report;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ReportRestControllerTests {

	@Autowired private MockMvc mockMvc;
	
	// 일주일간의 일별 매출 및 순이익 통계
	@Test
	@WithMockUser(username="user1", password="1234",authorities = {"판매관리자"})
	public void testGetReportDataLast7Days() throws Exception {
		String requestURL="/reports/sales_by_date/last_7_days";
		
		mockMvc.perform(get(requestURL)).andExpect(status().isOk()).andDo(print());
	}
	
	// 6개월간의 월별 매출 및 순이익 통계
	@Test
	@WithMockUser(username="user1", password="1234",authorities = {"판매관리자"})
	public void testGetReportDataLast6Months() throws Exception {
		String requestURL="/reports/sales_by_date/last_6_months";
		
		mockMvc.perform(get(requestURL)).andExpect(status().isOk()).andDo(print());
	}
	
	// 관리자가 특정한 날짜를 기준으로 판매량 통계
	@Test
	@WithMockUser(username="user1", password="1234",authorities = {"판매관리자"})
	public void testGetReportDataByDateRange() throws Exception {
		String startDate="2022-01-01";
		String endDate="2022-01-15";
		String requestURL="/reports/sales_by_date/"+startDate+"/"+endDate;
		
		mockMvc.perform(get(requestURL)).andExpect(status().isOk()).andDo(print());
	}
	
	// 7일간의 카테고리별 통계
	@Test
	@WithMockUser(username="user1", password="1234",authorities = {"판매관리자"})
	public void testGetReportDateByCategory() throws Exception {
		String requestURL="/reports/category/last_7_days";
		
		mockMvc.perform(get(requestURL)).andExpect(status().isOk()).andDo(print());
	}
	
	// 7일간의 상품별 통계
	@Test
	@WithMockUser(username="user1", password="1234",authorities = {"판매관리자"})
	public void testGetReportDateByProduct() throws Exception {
		String requestURL="/reports/product/last_7_days";
		
		mockMvc.perform(get(requestURL)).andExpect(status().isOk()).andDo(print());
	}
}
