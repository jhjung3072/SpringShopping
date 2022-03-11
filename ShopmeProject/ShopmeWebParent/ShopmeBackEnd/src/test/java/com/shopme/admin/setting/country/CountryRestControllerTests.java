package com.shopme.admin.setting.country;

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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.common.entity.Country;

@SpringBootTest
@AutoConfigureMockMvc

public class CountryRestControllerTests {

	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	CountryRepository repo;
	
	// 국가 리스트 불러오기
	@Test
	@WithMockUser(username="user1", password="pass1",authorities = {"운영자"})
	public void testListCountries() throws Exception {
		String url = "/countries/list";
		MvcResult result=mockMvc.perform(get(url))
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn();
		
		String jsonResponse=result.getResponse().getContentAsString();
		Country[] countries=objectMapper.readValue(jsonResponse,Country[].class);
	
		assertThat(countries).hasSizeGreaterThan(0);
	}
	
	// 국가 추가하기
	@Test
	@WithMockUser(username="user1", password="pass1",authorities = {"배송관리자"})
	public void testCreateCountry() throws JsonProcessingException, Exception {
		String url = "/countries/save";
		String countryName = "독일";
		String countryCode = "DE";
		Country country = new Country(countryName, countryCode);
		
		MvcResult result = mockMvc.perform(post(url).contentType("application/json")
				.content(objectMapper.writeValueAsString(country))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andReturn();
		
		String response = result.getResponse().getContentAsString();
		Integer countryId = Integer.parseInt(response);
		
		Optional<Country> findById = repo.findById(countryId);
		assertThat(findById.isPresent());
		
		Country savedCountry = findById.get();
		
		assertThat(savedCountry.getName()).isEqualTo(countryName);
	}
	
	// 국가 수정
	@Test
	@WithMockUser(username="user1", password="pass1",authorities = {"배송관리자"})
	public void testUpdateCountry() throws JsonProcessingException, Exception {
		String url = "/countries/save";
		Integer countryId=8;
		String countryName = "중국";
		String countryCode = "CN";
		Country country = new Country(countryId,countryName, countryCode);
		
		mockMvc.perform(post(url).contentType("application/json")
				.content(objectMapper.writeValueAsString(country))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().string(String.valueOf(countryId)));
		
		
		Optional<Country> findById = repo.findById(countryId);
		assertThat(findById.isPresent());
		
		Country savedCountry = findById.get();
		
		assertThat(savedCountry.getName()).isEqualTo(countryName);
	}
	
	// 국가 삭제
	@Test
	@WithMockUser(username="user1", password="pass1",authorities = {"배송관리자"})
	public void testDeleteCountry() throws Exception {
		Integer countryId = 8;
		String url = "/countries/delete/" + countryId;
		mockMvc.perform(get(url)).andExpect(status().isOk());
		
		Optional<Country> findById = repo.findById(countryId);
		
		assertThat(findById).isNotPresent();
	}
	
}
