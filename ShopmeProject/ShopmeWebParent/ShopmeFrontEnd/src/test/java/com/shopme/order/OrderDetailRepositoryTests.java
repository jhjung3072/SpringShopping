package com.shopme.order;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.shopme.common.entity.order.OrderStatus;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class OrderDetailRepositoryTests {

	@Autowired private OrderDetailRepository repo;
	
	// 해당 상품ID와 회원ID의 주문 추적 상태가 'DELIVERED' 상태인 OrderDetail 개수 반환 -> 1개 이상이면 리뷰 작성 가능
	@Test
	public void testCountByProductAndCustomerAndOrderStatus() {
		Integer productId = 99;
		Integer customerId=1;
		
		Long count=repo.countByProductAndCustomerAndOrderStatus(productId, customerId, OrderStatus.DELIVERED);
		assertThat(count).isGreaterThan(0);
	}
}
