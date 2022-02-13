package com.shopme.admin.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;

@RestController
public class ProductRestController {

	@Autowired private ProductService service;
	
	// 상품 중복 체크 POST
	@PostMapping("/products/check_unique")
	public String checkUnique(Integer id, String name) {
		return service.checkUnique(id, name);
	}	
	
	// 주문 수정페이지 -> 상품 추가에서 간략하게 보여줄때 사용 GET
	@GetMapping("/products/get/{id}")
	public ProductDTO getProductInfo(@PathVariable("id")Integer id) throws ProductNotFoundException {
		Product product=service.get(id);
		return new ProductDTO(product.getName(), product.getMainImagePath(), 
					product.getDiscountPrice(), product.getCost());
	}
}
