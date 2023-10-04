package com.e3ps;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.change.eo.dto.EoDTO;

public class Test {

	public static void main(String[] args) throws Exception {

		Class<?> clazz = EoDTO.class.getClass();
		Map<String, Object> map = new HashMap<>();
		for (Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true); // private 필드에 접근하기 위해 필요
			String fieldName = field.getName();
			Object fieldValue = field.get(EoDTO.class);
			map.put(fieldName, fieldValue);
		}
		System.out.println(map);
	}
}