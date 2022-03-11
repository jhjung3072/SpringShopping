package com.shopme.admin.user.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.category.CategoryRepository;
import com.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTests {
	@Autowired
	private CategoryRepository repo;
	
	// 최상위 카테고리 생성
	@Test
	public void testCreateRootCategory() {
		Category category = new Category("전자기기");
		Category savedCategory=repo.save(category);
		
		assertThat(savedCategory.getId()).isGreaterThan(0);
	}
	
	// 하위 카테고리 생성
	@Test
	public void testCreateSubCategory() {
		Category parent=new Category(7);
		Category subCategory=new Category("아이폰",parent);
		
		Category savedCategory=repo.save(subCategory);
		assertThat(savedCategory.getId()).isGreaterThan(0);
	}
	
	// 특정 카테고리 목록 출력 - 하위 카테고리 포함
	@Test
	public void testGetCategory() {
		Category category=repo.findById(3).get();
		System.out.println(category.getName());
		
		Set<Category> children=category.getChildren();
		
		for (Category subCategory:children) {
			System.out.println(subCategory.getName());
		}
		assertThat(children.size()).isGreaterThan(0);

	}
	
	// 모든 카테고리 목록 출력 - 하위 카테고리 포함
	@Test
	public void testPrintHierarchicalCategory() {
		Iterable<Category>categories=repo.findAll();
		
		for (Category category:categories) {
			if(category.getParent()==null) {
				System.out.println(category.getName());
				
				Set<Category>children=category.getChildren();
				for (Category subCategory : children) {
					System.out.println("--" + subCategory.getName());
					printChildren(subCategory, 1);
				}
			}
		}
	}
	
	// 하위 카테고리 구분자("--") 반복
	private void printChildren(Category parent, int subLevel) {
		int newSubLevel=subLevel+1;
		Set<Category>children=parent.getChildren();
		for (Category subCategory : children) {
			for (int i = 0; i<newSubLevel; i++) {
				System.out.print("--");
			}
			System.out.println(subCategory.getName());
			printChildren(subCategory, newSubLevel);
		}
	}
	
	// 최상위 카테고리 리스트 오름차순
	@Test
	public void testListRootCategories() {
		List<Category> rootCategories=repo.findRootCategories(Sort.by("name").ascending());
		rootCategories.forEach(cat -> System.out.println(cat.getName()));
	}
	
	// 카테고리 이름으로 검색
	@Test
	public void testFindByName() {
		String name="Computers1";
		Category category=repo.findByName(name);
		
		assertThat(category).isNotNull();
		assertThat(category.getName()).isEqualTo(name);
	}
	
	// 카테고리 줄임말로 검색
	@Test
	public void testFindByAlias() {
		String alias="Electronics2";
		
		Category category=repo.findByAlias(alias);
		
		assertThat(category).isNotNull();
		assertThat(category.getAlias()).isEqualTo(alias);
	}
}
