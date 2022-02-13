package com.shopme.admin.category;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Category;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {
	
	//상위 카테고리 이름 순으로 정렬
	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
	public List<Category> findRootCategories(Sort sort);

	// 상위 카테고리 페이징
	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
	public Page<Category> findRootCategories(Pageable pageable);
	
	// 카테고리 이름 검색 with 페이징
	@Query("SELECT c FROM Category c WHERE c.name LIKE %?1%")
	public Page<Category> search(String keyword, Pageable pageable);
	
	public Long countById(Integer id);
	
	public Category findByName(String name);
	
	public Category findByAlias(String alias);
	
	// 카테고리 활성화 enable or not
	@Query("UPDATE Category c SET c.enabled = ?2 WHERE c.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);	
}
