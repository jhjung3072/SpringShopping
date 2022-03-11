package com.shopme.admin.setting.state;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;

@SpringBootTest
@AutoConfigureMockMvc
public class StateRestControllerTests {
	
	@Autowired MockMvc mockMvc;
	
	@Autowired ObjectMapper objectMapper;
	
	@Autowired CountryRepository countryRepo;
	
	@Autowired StateRepository stateRepo;
	
	// 국가별 도시 리스트 목록
	@Test
	@WithMockUser(username="user1", password="pass1",authorities = {"운영자"})
	public void testListByCountries() throws Exception {
		Integer countryId = 14; // 호주
		String url = "/states/list_by_country/" + countryId;
		
		MvcResult result = mockMvc.perform(get(url))
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn();
		
		String jsonResponse = result.getResponse().getContentAsString();
		State[] states = objectMapper.readValue(jsonResponse, State[].class);
		
		assertThat(states).hasSizeGreaterThan(1);
	}
	
	// 도시 추가
	@Test
	@WithMockUser(username="user1", password="pass1",authorities = {"운영자"})
	public void testCreateState() throws Exception {
		String url = "/states/save";
		Integer countryId = 14; //호주
		Country country = countryRepo.findById(countryId).get();
		State state = new State("호주 신도시", country);
		
		MvcResult result = mockMvc.perform(post(url).contentType("application/json")
				.content(objectMapper.writeValueAsString(state))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andReturn();
		
		String response = result.getResponse().getContentAsString();
		Integer stateId = Integer.parseInt(response);
		Optional<State> findById = stateRepo.findById(stateId);
		
		assertThat(findById.isPresent());		
	}
	
	// 도시 이름 수정
	@Test
	@WithMockUser(username="user1", password="pass1",authorities = {"운영자"})
	public void testUpdateState() throws Exception {
		String url = "/states/save";
		Integer stateId = 9;
		String stateName = "호주신도시2";
		
		State state = stateRepo.findById(stateId).get();
		state.setName(stateName);
		
		mockMvc.perform(post(url).contentType("application/json")
			.content(objectMapper.writeValueAsString(state))
			.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().string(String.valueOf(stateId)));
		
		Optional<State> findById = stateRepo.findById(stateId);
		assertThat(findById.isPresent());
		
		State updatedState = findById.get();
		assertThat(updatedState.getName()).isEqualTo(stateName);
		
	}
	
	// 도시 삭제
	@Test
	@WithMockUser(username="user1", password="pass1",authorities = {"운영자"})
	public void testDeleteState() throws Exception {
		Integer stateId = 9; // 호주신도시2
		String uri = "/states/delete/" + stateId;
		
		mockMvc.perform(get(uri)).andExpect(status().isOk());
		
		Optional<State> findById = stateRepo.findById(stateId);
		
		assertThat(findById).isNotPresent();
	}	
}
