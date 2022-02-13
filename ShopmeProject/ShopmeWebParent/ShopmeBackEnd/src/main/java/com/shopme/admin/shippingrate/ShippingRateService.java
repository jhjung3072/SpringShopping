package com.shopme.admin.shippingrate;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.product.ProductRepository;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.entity.product.Product;

@Service
@Transactional
public class ShippingRateService {
	public static final int RATES_PER_PAGE = 10;
	private static final int DIM_DIVISOR = 139; // 해외 배송업체 Fedex 표준 dim 값
	@Autowired private ShippingRateRepository shipRepo;
	@Autowired private CountryRepository countryRepo;
	@Autowired private ProductRepository poductRepo;
	
	// 배송비 목록 페이징
	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		helper.listEntities(pageNum, RATES_PER_PAGE, shipRepo);
	}	
	
	// 국가 목록 리스트
	public List<Country> listAllCountries() {
		return countryRepo.findAllByOrderByNameAsc();
	}		
	
	// 배송비 저장
	public void save(ShippingRate rateInForm) throws ShippingRateAlreadyExistsException {
		ShippingRate rateInDB = shipRepo.findByCountryAndState(rateInForm.getCountry().getId(), rateInForm.getState());
		boolean foundExistingRateInNewMode = rateInForm.getId() == null && rateInDB != null; // 지금 새로 입력중인 배송비가 db에 이미 있다.
		boolean foundDifferentExistingRateInEditMode = rateInForm.getId() != null && rateInDB != null && !rateInDB.equals(rateInForm); // 지금 수정중인 배송비가 이미 db에 있고 서로 다른 개체다.
		
		if (foundExistingRateInNewMode || foundDifferentExistingRateInEditMode) {
			throw new ShippingRateAlreadyExistsException("해당 배송지의 배송비가 이미 설정되어있습니다: "
						+ rateInForm.getCountry().getName() + ", " + rateInForm.getState()); 					
		}
		shipRepo.save(rateInForm);
	}

	// 배송비 GET By ID
	public ShippingRate get(Integer id) throws ShippingRateNotFoundException {
		try {
			return shipRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new ShippingRateNotFoundException("해당 배송비ID를 찾을 수 없습니다.");
		}
	}
	
	// 배송비 착불 가능 여부 업데이트
	public void updateCODSupport(Integer id, boolean codSupported) throws ShippingRateNotFoundException {
		Long count = shipRepo.countById(id);
		if (count == null || count == 0) {
			throw new ShippingRateNotFoundException("해당 배송비ID를 찾을 수 없습니다.");
		}
		
		shipRepo.updateCODSupport(id, codSupported);
	}
	
	// 배송비 삭제
	public void delete(Integer id) throws ShippingRateNotFoundException {
		Long count = shipRepo.countById(id);
		if (count == null || count == 0) {
			throw new ShippingRateNotFoundException("해당 배송비ID를 찾을 수 없습니다.");
			
		}
		shipRepo.deleteById(id);
	}	
	
	// 주문 목록 수정에서 상품 추가시 배송비 계산
	public float calculateShippingCost(Integer productId, Integer countryId, String state) throws ShippingRateNotFoundException {
		ShippingRate shippingRate=shipRepo.findByCountryAndState(countryId, state);
		
		if (shippingRate==null) {
			throw new ShippingRateNotFoundException("해당 배송지의 배송비 기본값이 없습니다. 직접 입력해주세요.");
		}
		
		Product product=poductRepo.findById(productId).get();
		
		float dimWeight=(product.getLength() *product.getWidth() * product.getHeight()) / DIM_DIVISOR;
		float finalWeight= product.getWeight() > dimWeight ? product.getWeight() : dimWeight;
		
		return finalWeight *shippingRate.getRate();
	}
}
