package com.shopme.admin.setting.country;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Country;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CountryRepositoryTests {
	
	@Autowired private CountryRepository repo;
	
	// 국가 생성
	@Test
	public void testCreateCountry() {
		Country country = repo.save(new Country("한국", "KOR"));
		assertThat(country).isNotNull();
		assertThat(country.getId()).isGreaterThan(0);
	}
	
	// 국가 리스트 오름차순
	@Test
	public void testListCountries() {
		List<Country> listCountries = repo.findAllByOrderByNameAsc();
		listCountries.forEach(System.out::println);
		
		assertThat(listCountries.size()).isGreaterThan(0);
	}

	// 국가 이름 수정
	@Test
	public void testUpdateCountry() {
		Integer id = 1;
		String name = "대한민국";
		
		Country country = repo.findById(id).get();
		country.setName(name);
		
		Country updatedCountry = repo.save(country);
		
		assertThat(updatedCountry.getName()).isEqualTo(name);
	}
	
	// 국가 Get By ID
	@Test
	public void testGetCountry() {
		Integer id = 1;		
		Country country = repo.findById(id).get();
		assertThat(country).isNotNull();
	}
	
	// 국가 삭제
	@Test
	public void testDeleteCountry() {
		Integer id = 1;
		repo.deleteById(id);
		
		Optional<Country> findById = repo.findById(id);
		assertThat(findById.isEmpty());
	}
}
