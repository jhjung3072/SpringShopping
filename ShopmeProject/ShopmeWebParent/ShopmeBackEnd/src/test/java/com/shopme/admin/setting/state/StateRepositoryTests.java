package com.shopme.admin.setting.state;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class StateRepositoryTests {

	@Autowired private StateRepository repo;
	@Autowired private TestEntityManager entityManager;
	
	// 한국에 도시 목록 생성
	@Test
	public void testCreateStatesInKorea() {
		Integer countryId = 1;
		Country country = entityManager.find(Country.class, countryId);
		
		//State state = repo.save(new State("Seoul", country));
		//State state = repo.save(new State("Incheon", country));
		State state = repo.save(new State("Busan", country));
		
		assertThat(state).isNotNull();
		assertThat(state.getId()).isGreaterThan(0);
	}
	
	// 미국에 도시 목록 생성
	@Test
	public void testCreateStatesInUS() {
		Integer countryId = 2;
		Country country = entityManager.find(Country.class, countryId);
		
		//State state = repo.save(new State("California", country));
		//State state = repo.save(new State("Texas", country));
		//State state = repo.save(new State("New York", country));
		State state = repo.save(new State("Washington", country));
		
		assertThat(state).isNotNull();
		assertThat(state.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListStatesByCountry() {
		Integer countryId = 2;
		Country country = entityManager.find(Country.class, countryId);
		List<State> listStates = repo.findByCountryOrderByNameAsc(country);
		
		listStates.forEach(System.out::println);
		
		assertThat(listStates.size()).isGreaterThan(0);
	}
	
	// 도시 이름 수정
	@Test
	public void testUpdateState() {
		Integer stateId = 1;
		String stateName = "Gyeong ki do";
		State state = repo.findById(stateId).get();
		
		state.setName(stateName);
		State updatedState = repo.save(state);
		
		assertThat(updatedState.getName()).isEqualTo(stateName);
	}
	
	// 도시 Get By ID
	@Test
	public void testGetState() {
		Integer stateId = 1;
		Optional<State> findById = repo.findById(stateId);
		assertThat(findById.isPresent());
	}
	
	// 도시 삭제
	@Test
	public void testDeleteState() {
		Integer stateId = 8;
		repo.deleteById(stateId);

		Optional<State> findById = repo.findById(stateId);
		assertThat(findById.isEmpty());		
	}
}
