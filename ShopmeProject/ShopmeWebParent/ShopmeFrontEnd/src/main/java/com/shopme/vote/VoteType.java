package com.shopme.vote;

public enum VoteType {
	UP {
		public String toString() { return "좋아요"; }
	},
	
	DOWN {
		public String toString() { return "싫어요"; }
	}
}
