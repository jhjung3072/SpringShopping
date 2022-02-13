package com.shopme.admin.order;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.order.OrderDetail;

public interface OrderDetailRepository extends CrudRepository<OrderDetail, Integer>{

	// 해당 기간에 주문을 카테고리별로 리스트 in 통계 페이지
	// category.name, quantity, productCost, shippingCost, subtotal 만 불러오기 위해 new 사용
	@Query("SELECT NEW com.shopme.common.entity.order.OrderDetail(d.product.category.name, d.quantity,"
			+ " d. productCost, d.shippingCost, d.subtotal)"
			+ " FROM OrderDetail d WHERE d.order.orderTime BETWEEN ?1 AND ?2")
	public List<OrderDetail> findWithCategoryAndTimeBetween(Date startTime, Date endTime);

	// 해당 기간에 주문을 상품별로 리스트 in 통계 페이지
	// quantity, product.name, productCost, shippingCost, subtotal 만 불러오기 위해 new 사용
	@Query("SELECT NEW com.shopme.common.entity.order.OrderDetail(d.quantity,d.product.name,"
			+ " d. productCost, d.shippingCost, d.subtotal)"
			+ " FROM OrderDetail d WHERE d.order.orderTime BETWEEN ?1 AND ?2")
	public List<OrderDetail> findWithProductAndTimeBetween(Date startTime, Date endTime);
}
