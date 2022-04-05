package com.shopme.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Question;
import com.shopme.common.entity.product.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

	// 활성화 된 상품 중에서 카테고리별 상품 리스트
	@Query("SELECT p FROM Product p WHERE p.enabled = true "
			+ "AND (p.category.id = ?1 OR p.category.allParentIDs LIKE %?2%)"
			+ " ORDER BY p.name ASC")
	public Page<Product> listByCategory(Integer categoryId, String categoryIDMatch, Pageable pageable);
	
	// 줄임말로 상품 리턴
	public Product findByAlias(String alias);
	
	// 검색 성능 개선을 위해 FULLTEXT INDEX 사용
	// JPA에서 MATCH AGAINST 사용 불가능, nativeQuery 사용
	// 참고 : https://geek-techiela.blogspot.com/2016/02/springboot-jpa-fulltextsearchnativequer.html
	@Query(value = "SELECT * FROM products WHERE enabled = true AND "
			+ "MATCH(name, alias, short_description, full_description) AGAINST (?1 IN BOOLEAN MODE )", 
			nativeQuery = true)
	public Page<Product> search(String keyword, Pageable pageable);
	
	// 상품 평균 평점과 리뷰 개수 업데이트
	@Query("Update Product p SET p.averageRating = COALESCE((SELECT AVG(r.rating) FROM Review r WHERE r.product.id = ?1), 0),"
			+ " p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.product.id =?1) "
			+ "WHERE p.id = ?1")
	@Modifying
	public void updateReviewCountAndAverageRating(Integer productId);
	
	public Page<Product> findAll(Pageable pageable);
}
