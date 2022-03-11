package com.shopme.setting;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.Country;

public interface CountryRepository extends CrudRepository<Country, Integer> {
	public List<Country> findAllByOrderByNameAsc();
	
	// 국가 코드로 국가 리턴
	@Query("SELECT c FROM Country c WHERE c.code = ?1")
	public Country findByCode(String code);
}
