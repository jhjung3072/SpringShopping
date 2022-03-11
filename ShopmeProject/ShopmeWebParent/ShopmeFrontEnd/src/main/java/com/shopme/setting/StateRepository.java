package com.shopme.setting;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;

public interface StateRepository extends CrudRepository<State, Integer> {
	
	// 국가별로 도시 리스트 가져오기
	public List<State> findByCountryOrderByNameAsc(Country country);
}
