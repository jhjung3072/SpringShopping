package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;
import com.shopme.common.exception.CategoryNotFoundException;

@Service
@Transactional
public class CategoryService {
	public static final int ROOT_CATEGORIES_PER_PAGE = 4;
	
	@Autowired
	private CategoryRepository repo;
	
	// 카테고리 목록 페이징
	// 상위 카테고리 이름순 -> 하위 카테고리 이름순
	public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNum, String sortDir,
			String keyword) {
		Sort sort = Sort.by("name");
		
		if (sortDir.equals("asc")) {
			sort = sort.ascending();
		} else if (sortDir.equals("desc")) {
			sort = sort.descending();
		}
		
		Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);
		
		Page<Category> pageCategories = null;
		
		//검색어가 있을 때
		if (keyword != null && !keyword.isEmpty()) {
			pageCategories = repo.search(keyword, pageable);	
		} else {
			// 검색어가 없을 시 상위카테고리 find
			pageCategories = repo.findRootCategories(pageable);
		}
		
		List<Category> rootCategories = pageCategories.getContent();
		
		pageInfo.setTotalElements(pageCategories.getTotalElements());
		pageInfo.setTotalPages(pageCategories.getTotalPages());
		
		//검색어가 있을 때
		if (keyword != null && !keyword.isEmpty()) {
			List<Category> searchResult = pageCategories.getContent();
			for (Category category : searchResult) {
				category.setHasChildren(category.getChildren().size() > 0);
			}
			
			return searchResult;
			
		} else {
			// 검색어가 없을 시 상위카테고리 정렬
			return listHierarchicalCategories(rootCategories, sortDir);
		}
	}
	
	// 카테고리 목록
	private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
		List<Category> hierarchicalCategories = new ArrayList<>();
		
		for (Category rootCategory : rootCategories) {
			// 데이터베이스에 영향을 주지않고 "--" 을 삽입하기 위해 copy 사용
			hierarchicalCategories.add(Category.copyFull(rootCategory));
			
			// 상위 카테고리의 하위카테고리
			Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);
			
			// 첫번째 하위 카테고리에 "--" 삽입
			for (Category subCategory : children) {
				String name = "--" + subCategory.getName();
				hierarchicalCategories.add(Category.copyFull(subCategory, name));
				
				// 그 하위 카테고리에 "--" 삽입 재귀반복
				listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);
			}
		}
		
		return hierarchicalCategories;
	}
	
	// 하위카테고리 레벨 별 "--" 삽입 재귀 반복
	private void listSubHierarchicalCategories(List<Category> hierarchicalCategories,
			Category parent, int subLevel, String sortDir) {
		Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
		int newSubLevel = subLevel + 1;
		
		for (Category subCategory : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {				
				name += "--";
			}
			name += subCategory.getName();
		
			hierarchicalCategories.add(Category.copyFull(subCategory, name));
			
			//하위 카테고리에 재귀 반복
			listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);
		}
		
	}
	
	// 카테고리 저장
	// 상위 카테고리가 있을 시 상위카테고리 ID 도 저장
	public Category save(Category category) {
		Category parent = category.getParent();
		if (parent != null) {
			String allParentIds = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
			allParentIds += String.valueOf(parent.getId()) + "-";
			category.setAllParentIDs(allParentIds);
		}
		
		return repo.save(category);
	}
	
	// 카테고리 폼에서 사용하기 위한 카테고리 리스트
	public List<Category> listCategoriesUsedInForm() {
		List<Category> categoriesUsedInForm = new ArrayList<>();
		
		//상위 카테고리 이름순으로 정렬
		Iterable<Category> categoriesInDB = repo.findRootCategories(Sort.by("name").ascending());
		
		for (Category category : categoriesInDB) {
			// ID와 이름만 필요
			categoriesUsedInForm.add(Category.copyIdAndName(category));
			
			Set<Category> children = sortSubCategories(category.getChildren());
			
			//하위 카테고리 레벨 별로 "--" 삽입 재귀 반복
			for (Category subCategory : children) {
				String name = "--" + subCategory.getName();
				categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
				
				listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, 1);
			}
		}		
		
		return categoriesUsedInForm;
	}
	
	// 하위 카테고리 "--" 삽입 재귀 반복
	private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm, 
			Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = sortSubCategories(parent.getChildren());
		
		for (Category subCategory : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {				
				name += "--";
			}
			name += subCategory.getName();
			
			categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
			
			listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, newSubLevel);
		}		
	}	
	
	// 카테고리 GET by id
	public Category get(Integer id) throws CategoryNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new CategoryNotFoundException("카테고리 ID: " + id + "을 찾을 수 없습니다.");
		}
	}
	
	// 카테고리 이름 및 줄임말 중복체크
	// DuplicateName or DuplicateAlias or OK
	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id == 0);
		
		Category categoryByName = repo.findByName(name);
		
		if (isCreatingNew) {
			if (categoryByName != null) {
				return "DuplicateName";
			} else {
				Category categoryByAlias = repo.findByAlias(alias);
				if (categoryByAlias != null) {
					return "DuplicateAlias";	
				}
			}
		} else {
			if (categoryByName != null && categoryByName.getId() != id) {
				return "DuplicateName";
			}
			
			Category categoryByAlias = repo.findByAlias(alias);
			if (categoryByAlias != null && categoryByAlias.getId() != id) {
				return "DuplicateAlias";
			}
			
		}
		
		return "OK";
	}
	
	// 하위카테고리 이름 오름차순 정렬
	private SortedSet<Category> sortSubCategories(Set<Category> children) {
		return sortSubCategories(children, "asc");
	}
	
	// 하위카테고리 이름순 정렬
	private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
		SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
			@Override
			public int compare(Category cat1, Category cat2) {
				// 오름차순
				if (sortDir.equals("asc")) {
					return cat1.getName().compareTo(cat2.getName());
				} else {
					// 내림차순
					return cat2.getName().compareTo(cat1.getName());
				}
			}
		});
		
		sortedChildren.addAll(children);
		
		return sortedChildren;
	}
	
	// 카테고리 활성화
	public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
		repo.updateEnabledStatus(id, enabled);
	}
	
	// 카테고리 삭제 by id
	public void delete(Integer id) throws CategoryNotFoundException {
		Long countById = repo.countById(id);
		if (countById == null || countById == 0) {
			throw new CategoryNotFoundException("카테고리 ID: " + id + "을 찾을 수 없습니다.");
		}
		
		repo.deleteById(id);
	}	
}
