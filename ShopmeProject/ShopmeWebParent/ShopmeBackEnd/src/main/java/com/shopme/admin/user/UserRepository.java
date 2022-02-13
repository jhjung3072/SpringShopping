package com.shopme.admin.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.common.entity.User;

public interface UserRepository extends SearchRepository<User, Integer> {
	
	// 이메일로 직원 찾기
	@Query("SELECT u FROM User u WHERE u.email = :email")
	public User getUserByEmail(@Param("email") String email);
	
	public Long countById(Integer id);
	
	// 유저 키워드 검색(id, 이메일, 이름)
	@Query("SELECT u FROM User u WHERE CONCAT(u.id, u.email, u.firstName,"
			+ " u.lastName) LIKE %?1%")
	public Page<User> findAll(String keyword, Pageable pageable);
	
	// 직원 활성화 업데이트
	@Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);
}
