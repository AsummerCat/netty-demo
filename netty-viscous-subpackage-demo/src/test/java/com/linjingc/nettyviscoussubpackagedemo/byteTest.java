package com.linjingc.nettyviscoussubpackagedemo;

import java.io.UnsupportedEncodingException;
import java.util.Date;

public class byteTest {
	public static void main(String[] args) throws UnsupportedEncodingException {
		String s1 =new Date() + ":hello world by FixedLengthFrameDecoder"+" num"+1000;
		byte[] bytes1 = s1.getBytes("UTF-8");
		System.out.println(bytes1.length);

		String s2 = "小明";
		byte[] bytes2 = s2.getBytes("UTF-8");
		System.out.println(bytes2.length);

	}
}
