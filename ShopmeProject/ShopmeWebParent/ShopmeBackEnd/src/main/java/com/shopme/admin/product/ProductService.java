package com.shopme.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;

@Service
@Transactional
public class ProductService {
	public static final int PRODUCTS_PER_PAGE = 5;
	
	@Autowired private ProductRepository repo;
	
	// 상품 목록
	public List<Product> listAll() {
		return (List<Product>) repo.findAll();
	}
	
	// 상품 목록 페이징
	public void listByPage(int pageNum, PagingAndSortingHelper helper, Integer categoryId) {
		Pageable pageable = helper.createPageable(PRODUCTS_PER_PAGE, pageNum);
		String keyword = helper.getKeyword();
		Page<Product> page = null;
		
		// 검색어가 있다면
		if (keyword != null && !keyword.isEmpty()) {
			// 상위카테고리가 있다면
			if (categoryId != null && categoryId > 0) {
				// 카테고리끼리 묶어서 페이징
				String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
				page = repo.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
			} else {
				page = repo.findAll(keyword, pageable);
			}
		} else {
			// 검색어가 없다면
			if (categoryId != null && categoryId > 0) {
				String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
				// 모든 카테고리 페이징
				page = repo.findAllInCategory(categoryId, categoryIdMatch, pageable);
			} else {		
				page = repo.findAll(pageable);
			}
		}
		
		helper.updateModelAttributes(pageNum, page);
	}	
	
	// 상품 검색
	public void searchProducts(int pageNum, PagingAndSortingHelper helper) {
		Pageable pageable = helper.createPageable(PRODUCTS_PER_PAGE, pageNum);
		String keyword = helper.getKeyword();		
		Page<Product> page = repo.searchProductsByName(keyword, pageable);		
		helper.updateModelAttributes(pageNum, page);
	}
	
	// 상품 저장
	public Product save(Product product) {
		if (product.getId() == null) {
			product.setCreatedTime(new Date());
		}
		
		// 상품 줄임말이 비어있다면, 상품명의 공백을 "-" 로 치환
		if (product.getAlias() == null || product.getAlias().isEmpty()) {
			String defaultAlias = product.getName().replaceAll(" ", "-");
			product.setAlias(defaultAlias);
		} else {
			product.setAlias(product.getAlias().replaceAll(" ", "-"));
		}
		
		product.setUpdatedTime(new Date());
		
		Product updatedProduct = repo.save(product);
		repo.updateReviewCountAndAverageRating(updatedProduct.getId());
		
		return updatedProduct;
	}
	
	// 상품 가격저장
	public void saveProductPrice(Product productInForm) {
		Product productInDB = repo.findById(productInForm.getId()).get();
		productInDB.setCost(productInForm.getCost());
		productInDB.setPrice(productInForm.getPrice());
		productInDB.setDiscountPercent(productInForm.getDiscountPercent());
		
		repo.save(productInDB);
	}
	
	// 상품 이름 중복확인
	// Duplicate or OK
	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0);
		Product productByName = repo.findByName(name);
		
		if (isCreatingNew) {
			if (productByName != null) return "Duplicate";
		} else {
			if (productByName != null && productByName.getId() != id) {
				return "Duplicate";
			}
		}
		
		return "OK";
	}
	
	// 상품 활성화 수정
	public void updateProductEnabledStatus(Integer id, boolean enabled) {
		repo.updateEnabledStatus(id, enabled);
	}
	
	// 상품 삭제
	public void delete(Integer id) throws ProductNotFoundException {
		Long countById = repo.countById(id);
		
		if (countById == null || countById == 0) {
			throw new ProductNotFoundException("해당 상품ID를 찾을 수 없습니다.");			
		}
		
		repo.deleteById(id);
	}	
	
	// 상품 GET by ID
	public Product get(Integer id) throws ProductNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new ProductNotFoundException("해당 상품ID를 찾을 수 없습니다.");
		}
	}
}
