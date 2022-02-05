package com.shopme.review.vote;

public enum VoteType {
	UP {
		public String toString() { return "추천"; }
	},
	
	DOWN {
		public String toString() { return "비추천"; }
	}
}
