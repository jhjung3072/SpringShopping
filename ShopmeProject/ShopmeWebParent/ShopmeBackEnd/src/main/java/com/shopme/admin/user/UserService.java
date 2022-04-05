package com.shopme.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Service
@Transactional
public class UserService {
	public static final int USERS_PER_PAGE = 4;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	// 직원 GET by 이메일
	public User getByEmail(String email) {
		return userRepo.getUserByEmail(email);
	}
	
	// 직원 목록
	public List<User> listAll() {
		return (List<User>) userRepo.findAll(Sort.by("firstName").ascending());
	}
	
	// 직원 목록 페이징
	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		helper.listEntities(pageNum, USERS_PER_PAGE, userRepo);
	}
	
	// 권한 목록 리스트
	public List<Role> listRoles() {
		return (List<Role>) roleRepo.findAll();
	}

	// 직원 저장
	public User save(User user) {
		boolean isUpdatingUser = (user.getId() != null); // 직원을 정보를 수정중. 새로 생성x
		
		// 직원 정보를 수정중이라면
		if (isUpdatingUser) {
			User existingUser = userRepo.findById(user.getId()).get();
			
			if (user.getPassword().isEmpty()) { // 패스워드 칸이 비어져있다면
				user.setPassword(existingUser.getPassword()); // 기존의 패스워드를 그대로 사용
			} else { // 패스워드 칸이 채워져있다면(수정)
				encodePassword(user); // 암호화
			}
			
		} else { // 직원 정보를 새로 생성중이라면
			encodePassword(user); // 암호화
		}
		
		return userRepo.save(user);
	}
	
	// 직원 정보 수정
	public User updateAccount(User userInForm) {
		User userInDB = userRepo.findById(userInForm.getId()).get();
		
		// 수정폼에 패스워드가 채워져있다면 db에 저장후 암호화
		if (!userInForm.getPassword().isEmpty()) {
			userInDB.setPassword(userInForm.getPassword());
			encodePassword(userInDB);
		}
		
		// 수정폼에 사진이 있다면, db에 사진 수정
		if (userInForm.getPhotos() != null) {
			userInDB.setPhotos(userInForm.getPhotos());
		}
		
		// 이름 수정
		userInDB.setFirstName(userInForm.getFirstName());
		userInDB.setLastName(userInForm.getLastName());
		
		return userRepo.save(userInDB);
	}
	
	// 패스워드 암호화
	private void encodePassword(User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}
	
	// 직원 이메일 중복 확인
	public boolean isEmailUnique(Integer id, String email) {
		User userByEmail = userRepo.getUserByEmail(email);
		
		// 기존 db에 해당 이메일이 없다면 true
		if (userByEmail == null) return true;
		
		boolean isCreatingNew = (id == null);
		
		if (isCreatingNew) { // 새로 생성중인 폼에 이미 이메일이 존재한다면 false
			if (userByEmail != null) return false;
		} else { // 수정중인 폼인데 이메일의 id가 다르다면(개체가 서로 다르다면) false
			if (userByEmail.getId() != id) {
				return false;
			}
		}
		
		return true;
	}

	// 직원 GET by ID
	public User get(Integer id) throws UserNotFoundException {
		try {
			return userRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new UserNotFoundException("해당 ID의 직원 찾을 수 없습니다 ID: "+id);
		}
	}
	
	// 직원 삭제
	public void delete(Integer id) throws UserNotFoundException {
		Long countById = userRepo.countById(id);
		if (countById == null || countById == 0) {
			throw new UserNotFoundException("해당 ID의 직원 찾을 수 없습니다 ID: "+id);
		}
		
		userRepo.deleteById(id);
	}
	
	// 직원 활성화 여부 업데이트
	public void updateUserEnabledStatus(Integer id, boolean enabled) {
		userRepo.updateEnabledStatus(id, enabled);
	}
}
