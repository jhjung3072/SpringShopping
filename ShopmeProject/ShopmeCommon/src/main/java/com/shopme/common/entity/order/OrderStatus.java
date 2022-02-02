package com.shopme.common.entity.order;

public enum OrderStatus {
	
	NEW {
		@Override
		public String defaultDescription() {
			return "주문이 접수되었습니다.";
		}
		
	}, 
	
	CANCELLED {
		@Override
		public String defaultDescription() {
			return "주문이 거부되었습니다.";
		}
	}, 
	
	PROCESSING {
		@Override
		public String defaultDescription() {
			return "주문이 진행중입니다.";
		}
	},
	
	PACKAGED {
		@Override
		public String defaultDescription() {
			return "상품이 포장되었습니다.";
		}		
	}, 
	
	PICKED {
		@Override
		public String defaultDescription() {
			return "배달담당자가 상품을 픽업했습니다.";
		}		
	}, 
	
	SHIPPING {
		@Override
		public String defaultDescription() {
			return "배달담당자가 상품을 배송중입니다.";
		}		
	},
	
	DELIVERED {
		@Override
		public String defaultDescription() {
			return "회원이 상품을 수령했습니다.";
		}		
	}, 
	
	RETURN_REQUESTED {
		@Override
		public String defaultDescription() {
			return "회원이 반품을 요청했습니다.";
		}		
	},
	
	RETURNED {
		@Override
		public String defaultDescription() {
			return "상품이 반품되었습니다.";
		}		
	}, 
	
	PAID {
		@Override
		public String defaultDescription() {
			return "회원이 상품을 결제했습니다.d";
		}		
	}, 
	
	REFUNDED {
		@Override
		public String defaultDescription() {
			return "환불이 완료되었습니다.";
		}		
	};
	
	public abstract String defaultDescription();
}
