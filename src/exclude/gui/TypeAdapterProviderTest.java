// TODO - RAP-GEN
//package com.ecfeed.core.operations;
//
//import static org.junit.Assert.*;
//
//import org.junit.Test;
//
//import com.ecfeed.core.type.adapter.ITypeAdapter;
//import com.ecfeed.core.type.adapter.TypeAdapterProvider;
//
//public class TypeAdapterProviderTest {
//
//	@SuppressWarnings("unchecked")
//	@Test
//	public void generateStringValueTest() {
//
//		ITypeAdapter<String> adapter = (ITypeAdapter<String>) new TypeAdapterProvider().getAdapter("String");
//
//		String regex = "[ab]{4,6}c";
//		String value = adapter.generateValue(regex);
//		assertTrue(value.matches(regex));
//	}
//
//}
