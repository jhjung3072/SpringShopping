package com.shopme.admin.setting.state;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;

public interface StateRepository extends CrudRepository<State, Integer> {
	
	// 국가별 도시 리스트
	public List<State> findByCountryOrderByNameAsc(Country country);
}
