package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest(showSql=false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
	
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	// 직원 생성 - 역할 1개(운영자)
	@Test
	public void testCreateNewUserWithOneRole() {
		Role roleAdmin=entityManager.find(Role.class, 1);
		User user1=new User("jaeho@naver.com","jaeho3072","jeong","jaeho");
		user1.addRole(roleAdmin);
		
		User savedUser=repo.save(user1);
		assertThat(savedUser.getId()).isGreaterThan(0);
		
	}
	
	// 유저 생성 - 역할 1개(편집자, Q/A담당자)
	@Test
	public void testCreateNewUserWithTwoRoles() {
		User user2 = new User("user2@gmail.com","user3072","hoo","hee");
		Role roleEditor=new Role(3);
		Role roleAssistant=new Role(5);
		user2.addRole(roleEditor);
		user2.addRole(roleAssistant);
		
		User savedUser=repo.save(user2);
		assertThat(savedUser.getId()).isGreaterThan(0);
		
	}
	
	// 직원 목록 리스트
	@Test
	public void testListAllUsers() {
		Iterable<User> listUsers=repo.findAll();
		listUsers.forEach(user -> System.out.println(user));
	}
	
	// 직원 Get By ID
	@Test
	public void testGetUserById() {
		User user=repo.findById(1).get();
		System.out.println(user);
		assertThat(user).isNotNull();
	}
	
	// 직원 활성화 및 이메일 수정
	@Test
	public void testUpdateUserDetails() {
		User user=repo.findById(1).get();
		user.setEnabled(true);
		user.setEmail("test@gmail.com");
		
		repo.save(user);
		
	}
	
	// 직원 역할 수정
	@Test
	public void testUpdateUserRoles() {
		User user=repo.findById(2).get();
		Role roleEditor=new Role(3);
		Role roleSaleperson=new Role(2);
		user.getRoles().remove(roleEditor);
		user.addRole(roleSaleperson);
		
		repo.save(user);
	}
	
	// 직원 삭제
	@Test
	public void testDeleteUser() {
		Integer userId=2;
		repo.deleteById(userId);
	}
	
	// 직원 Get By 이메일
	@Test
	public void testGetUserByEmail() {
		String email = "test@gmail.com";
		User user=repo.getUserByEmail(email);
		assertThat(user).isNotNull();
	}
	
	// 직원 찾기 Get By ID
	@Test
	public void testCountById() {
		Integer id = 1;
		Long countById= repo.countById(id);
		assertThat(countById).isNotNull().isGreaterThan(0);
	}
	
	// 직원 비활성화
	@Test
	public void testDisableUser() {
		Integer id=1;
		repo.updateEnabledStatus(id, false);
		
	}
	
	// 직원 활성화
	@Test
	public void testEnableUser() {
		Integer id=3;
		repo.updateEnabledStatus(id, true);
		
	}
	
	// 직원 목록 페이징
	@Test
	public void testListFirstPage() {
		int pageNumber=2;
		int pageSize=4;
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page=repo.findAll(pageable);
		
		List<User>listUsers=page.getContent();
		
		listUsers.forEach(user->System.out.println(user));
		
		assertThat(listUsers.size()).isEqualTo(pageSize);
	}
	
	// 직원 키워드 검색 및 페이징
	@Test
	public void testSearchUsers() {
		String keyword = "jaeho";
		
		int pageNumber=0;
		int pageSize=4;
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page=repo.findAll(keyword,pageable);
		
		List<User>listUsers=page.getContent();
		
		listUsers.forEach(user->System.out.println(user));
		
		assertThat(listUsers.size()).isGreaterThan(0);
		
	}

}
