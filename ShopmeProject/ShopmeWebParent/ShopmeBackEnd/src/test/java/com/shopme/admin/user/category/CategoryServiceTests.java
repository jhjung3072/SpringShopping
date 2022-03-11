package com.shopme.admin.user.category;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shopme.admin.category.CategoryRepository;
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Category;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {
	
	@MockBean
	private CategoryRepository repo;
	
	@InjectMocks
	private CategoryService service;
	
	// 카테고리 생성 시 이름 중복
	@Test
	public void testCheckUniqueInNewModeReturnDuplicateName() {
		Integer id=null;
		String name="Computers";
		String alias="abc";
		
		Category category =new Category(id, name, alias);
		
		// checkUnique 에서 repo.findByName 이 실행될 때 category 리턴
		Mockito.when(repo.findByName(name)).thenReturn(category);
		// checkUnique 에서 repo.findByAlias 가 실행될 때 null 리턴
		Mockito.when(repo.findByAlias(alias)).thenReturn(null);
		
		String result=service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("DuplicateName");
	}
	
	// 카테고리 생성 시 줄임말 중복
	@Test
	public void testCheckUniqueInNewModeReturnDuplicateAlias() {
		Integer id=null;
		String name="zzzzz";
		String alias="Computers";
		
		Category category =new Category(id, name, alias);
		
		// checkUnique 에서 repo.findByName 이 실행될 때 null 리턴
		Mockito.when(repo.findByName(name)).thenReturn(null);
		// checkUnique 에서 repo.findByAlias 가 실행될 때 category 리턴
		Mockito.when(repo.findByAlias(alias)).thenReturn(category);
		
		String result=service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("DuplicateAlias");
	}
	
	// 카테고리 생성 시 중복아닐때
	@Test
	public void testCheckUniqueInNewModeReturnOk() {
		Integer id=null;
		String name="zzzzz";
		String alias="Computers";
		
		Category category =new Category(id, name, alias);
		
		// checkUnique 에서 repo.findByName 이 실행될 때 null 리턴
		Mockito.when(repo.findByName(name)).thenReturn(null);
		// checkUnique 에서 repo.findByAlias 가 실행될 때 null 리턴
		Mockito.when(repo.findByAlias(alias)).thenReturn(null);
		
		String result=service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("OK");
	}
	
	// 카테고리 수정 시 이름 중복
	@Test
	public void testCheckUniqueInEditModeReturnDuplicateName() {
		Integer id=1;
		String name="Computers";
		String alias="abc";
		
		Category category =new Category(2, name, alias);
		
		// checkUnique 에서 repo.findByName 이 실행될 때 category 리턴
		Mockito.when(repo.findByName(name)).thenReturn(category);
		// checkUnique 에서 repo.findByAlias 가 실행될 때 null 리턴
		Mockito.when(repo.findByAlias(alias)).thenReturn(null);
		
		String result=service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("DuplicateName");
	}
	
	// 카테고리 수정 시 줄임말 중복
	@Test
	public void testCheckUniqueInEditModeReturnDuplicateAlias() {
		Integer id=1;
		String name="zzzzz";
		String alias="Computers";
		
		Category category =new Category(2, name, alias);
		
		// checkUnique 에서 repo.findByName 이 실행될 때 null 리턴
		Mockito.when(repo.findByName(name)).thenReturn(null);
		// checkUnique 에서 repo.findByAlias 가 실행될 때 category 리턴
		Mockito.when(repo.findByAlias(alias)).thenReturn(category);
		
		String result=service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("DuplicateAlias");
	}
	
	// 카테고리 수정 시 중복아닐 때
	@Test
	public void testCheckUniqueInEditModeReturnOk() {
		Integer id=1;
		String name="zzzzz";
		String alias="Computers";
		
		Category category =new Category(id, name, alias);
		
		// checkUnique 에서 repo.findByName 이 실행될 때 null 리턴
		Mockito.when(repo.findByName(name)).thenReturn(null);
		// checkUnique 에서 repo.findByAlias 가 실행될 때 null 리턴
		Mockito.when(repo.findByAlias(alias)).thenReturn(null);
		
		String result=service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("OK");
	}
}
