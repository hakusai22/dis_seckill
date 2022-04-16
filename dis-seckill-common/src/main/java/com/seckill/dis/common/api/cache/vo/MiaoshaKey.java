package com.seckill.dis.common.api.cache.vo;

public class MiaoshaKey extends BaseKeyPrefix{

	private MiaoshaKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	public static MiaoshaKey isGoodsOver = new MiaoshaKey(0, "go");
	public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60, "mp");
	public static MiaoshaKey getMiaoshaVerifyCode = new MiaoshaKey(300, "vc");

	// 需要设置过期时间，对应为业务正常完成的时间。
	public static MiaoshaKey robRedisStock = new MiaoshaKey(10, "rrs");


	public static MiaoshaKey getMiaoshaMessage = new MiaoshaKey(0, "me");
	public static MiaoshaKey getResendCount = new MiaoshaKey(0, "rsc");

	public static MiaoshaKey isConsumed = new MiaoshaKey(0, "con");



}
