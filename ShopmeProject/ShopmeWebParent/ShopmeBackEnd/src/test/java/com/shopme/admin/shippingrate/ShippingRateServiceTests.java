package com.shopme.admin.shippingrate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shopme.admin.product.ProductRepository;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.entity.product.Product;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class ShippingRateServiceTests {

	@MockBean private ShippingRateRepository shipRepo;
	@MockBean private ProductRepository productRepo;
	
	@InjectMocks
	private ShippingRateService shipService;
	
	//  배송비 계산 예외 처리 - 등록되지 않은 도시
	@Test
	public void testCalculateShippingCost_NoRateFound() {
		Integer productId = 1;
		Integer countryId = 14;
		String state = "ABCD"; // 등록되지 않은 도시
		
		// calculateShippingCost 에서 shipRepo.findByCountryAndState 가 실행될때 null
		Mockito.when(shipRepo.findByCountryAndState(countryId, state)).thenReturn(null);
		
		assertThrows(ShippingRateNotFoundException.class, new Executable() {
			
			@Override
			public void execute() throws Throwable {
				shipService.calculateShippingCost(productId, countryId, state);
			}
		});
	}
	
	// 배송비 계산 - 등록된 도시
	@Test
	public void testCalculateShippingCost_RateFound() throws ShippingRateNotFoundException {
		Integer productId = 1;
		Integer countryId = 234;
		String state = "New York";
		
		ShippingRate shippingRate = new ShippingRate();
		shippingRate.setRate(10);
		
		// calculateShippingCost 에서 shipRepo.findByCountryAndState 가 실행될 때 ShippingRate 리턴
		Mockito.when(shipRepo.findByCountryAndState(countryId, state)).thenReturn(shippingRate);
		
		Product product = new Product();
		product.setWeight(5);
		product.setWidth(4);
		product.setHeight(3);
		product.setLength(8); // 총 배송비 50
		
		// calculateShippingCost 에서 productRepo.findById 가 실행될 때 product 객체 리턴
		Mockito.when(productRepo.findById(productId)).thenReturn(Optional.of(product));
	
		float shippingCost = shipService.calculateShippingCost(productId, countryId, state);
		
		assertEquals(50, shippingCost);
	}
}
