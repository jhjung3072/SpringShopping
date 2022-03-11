package com.shopme.order;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderRestControllerTests {

	@Autowired private MockMvc mockMvc;
	@Autowired private ObjectMapper objectMapper;
	
	// 주문 취소 요청 실패 - 해당 주문ID 없음
	@Test
	@WithUserDetails("jhjung3072@gmail.com")
	public void testSendOrderReturnRequestFailed() throws Exception {
		Integer orderId = 1111;
		OrderReturnRequest returnRequest = new OrderReturnRequest(orderId, "", "");
		
		String requestURL = "/orders/return";
		
		mockMvc.perform(post(requestURL)
						.with(csrf())
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(returnRequest)))
				.andExpect(status().isNotFound())
				.andDo(print());
	}
	
	// 주문 취소 요청 성공
	@Test
	@WithUserDetails("jhjung3072@gmail.com")
	public void testSendOrderReturnRequestSuccessful() throws Exception {
		Integer orderId = 3;
		String reason = "잘못골랐습니다.";
		String note = "환불해주십시오";
		
		OrderReturnRequest returnRequest = new OrderReturnRequest(orderId, reason, note);
		
		String requestURL = "/orders/return";
		
		mockMvc.perform(post(requestURL)
						.with(csrf())
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(returnRequest)))
				.andExpect(status().isOk())
				.andDo(print());
	}	
}
