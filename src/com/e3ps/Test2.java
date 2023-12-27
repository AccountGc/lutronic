package com.e3ps;

public class Test2 {

	public static void main(String[] args) throws Exception {

		String n = "Asdasd_vvvv";

		int idx = n.lastIndexOf("_");
		System.out.println(n.substring(0, idx));

	}

}
