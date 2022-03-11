package com.shopme.shipping;

import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;

public interface ShippingRateRepository extends CrudRepository<ShippingRate, Integer> {
	
	// 해당 국가와 도시로 배송비 가져오기
	public ShippingRate findByCountryAndState(Country country, String state);
}
