package com.shopme.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;
import com.shopme.common.exception.CategoryNotFoundException;

@Service
public class CategoryService {

	@Autowired private CategoryRepository repo;
	
	// 하위 카테고리가 없는 카테고리 리스트 리턴
	public List<Category> listNoChildrenCategories() {
		List<Category> listNoChildrenCategories = new ArrayList<>();
		
		List<Category> listEnabledCategories = repo.findAllEnabled();
		
		listEnabledCategories.forEach(category -> {
			Set<Category> children = category.getChildren();
			if (children == null || children.size() == 0) {
				listNoChildrenCategories.add(category);
			}
		});
		
		return listNoChildrenCategories;
	}
	
	// 카테고리 GET by alias and enabled
	public Category getCategory(String alias) throws CategoryNotFoundException {
		Category category = repo.findByAliasEnabled(alias);
		if (category == null) {
			throw new CategoryNotFoundException("해당 줄임말로된 카테고리가 없습니다: " + alias);
		}
		
		return category;
	}
	
	// 해당 카테고리의 상위 카테고리 리스트
	public List<Category> getCategoryParents(Category child) {
		List<Category> listParents = new ArrayList<>();
		
		Category parent = child.getParent();
		
		// 상위 카테고리 리스트에 저장, 반복
		// 맨 상위 카테고리를 맨 앞으로 저장
		while (parent != null) {
			listParents.add(0, parent);
			parent = parent.getParent();
		}
		
		listParents.add(child);
		
		return listParents;
	}
}
