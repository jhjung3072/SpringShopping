package com.shopme.admin.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.product.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {
	
	public Product findByName(String name);
	
	// 상품 활성화 수정
	@Query("UPDATE Product p SET p.enabled = ?2 WHERE p.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);
	
	public Long countById(Integer id);
	
	// 상품 키워드 검색 (이름, 짧은 설명, 자세한 설명, 브랜드의 이름, 카테고리의 이름)
	@Query("SELECT p FROM Product p WHERE p.name LIKE %?1% " 
			+ "OR p.shortDescription LIKE %?1% "
			+ "OR p.fullDescription LIKE %?1% "
			+ "OR p.brand.name LIKE %?1% "
			+ "OR p.category.name LIKE %?1%")
	public Page<Product> findAll(String keyword, Pageable pageable);

	// 모든 카테고리에서 상품 검색
	@Query("SELECT p FROM Product p WHERE p.category.id = ?1 "
			+ "OR p.category.allParentIDs LIKE %?2%")	
	public Page<Product> findAllInCategory(Integer categoryId, String categoryIdMatch, 
			Pageable pageable);
	
	// 특정 카테고리에서 상품 검색
	@Query("SELECT p FROM Product p WHERE (p.category.id = ?1 "
			+ "OR p.category.allParentIDs LIKE %?2%) AND "
			+ "(p.name LIKE %?3% " 
			+ "OR p.shortDescription LIKE %?3% "
			+ "OR p.fullDescription LIKE %?3% "
			+ "OR p.brand.name LIKE %?3% "
			+ "OR p.category.name LIKE %?3%)")			
	public Page<Product> searchInCategory(Integer categoryId, String categoryIdMatch, 
			String keyword, Pageable pageable);
	
	// 상품이름으로 상품 검색
	@Query("SELECT p FROM Product p WHERE p.name LIKE %?1%")
	public Page<Product> searchProductsByName(String keyword, Pageable pageable);
	
	// 해당 상품의 리뷰 평균값을 반환 or 값이 없으면 0반환, 해당 리뷰의 개수를 반환
	@Query("Update Product p SET p.averageRating = COALESCE((SELECT AVG(r.rating) FROM Review r WHERE r.product.id = ?1), 0),"
			+ " p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.product.id =?1) "
			+ "WHERE p.id = ?1")
	@Modifying
	public void updateReviewCountAndAverageRating(Integer productId);
}
