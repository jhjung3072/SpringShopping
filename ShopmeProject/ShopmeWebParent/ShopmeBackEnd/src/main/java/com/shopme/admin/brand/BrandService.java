package com.shopme.admin.brand;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.Brand;

@Service
public class BrandService {
	public static final int BRANDS_PER_PAGE = 10;
	
	@Autowired
	private BrandRepository repo;
	
	// 브랜드 목록 (not paged)
	public List<Brand> listAll() {
		return (List<Brand>) repo.findAll();
	}
	
	// 브랜드 목록 (paged)
	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		helper.listEntities(pageNum, BRANDS_PER_PAGE, repo);
	}
	
	// 브랜드 저장
	public Brand save(Brand brand) {
		return repo.save(brand);
	}
	
	// 브랜드 get
	public Brand get(Integer id) throws BrandNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new BrandNotFoundException("해당 브랜드ID를 찾을 수 없습니다. ID : ");
		}
	}
	
	// 브랜드 삭제
	public void delete(Integer id) throws BrandNotFoundException {
		Long countById = repo.countById(id);
		
		if (countById == null || countById == 0) {
			throw new BrandNotFoundException("해당 브랜드ID를 찾을 수 없습니다. ID : " + id);			
		}
		
		repo.deleteById(id);
	}
	
	// 브랜드 이름 중복 확인
	// Duplicate = 중복
	// OK = 중복 아님
	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0);
		Brand brandByName = repo.findByName(name);
		
		if (isCreatingNew) { // 새로 생성중이라면
			if (brandByName != null) return "Duplicate"; // 이름이 같다면 중복
		} else { // 새로 생성중이 아니라면(수정중)
			if (brandByName != null && brandByName.getId() != id) { // 이름이 같고, 수정중인 브랜드id가 아니라면 중복
				return "Duplicate";
			}
		}
		
		return "OK";
	}
}
