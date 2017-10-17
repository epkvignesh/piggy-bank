package com.piggy.bank.piggybankgroovy

import groovy.transform.CompileStatic
import groovy.transform.ToString
import groovy.transform.builder.Builder

@CompileStatic
@Builder
@ToString(includePackage = false)
class Response {
	int statusCode
	String body
	Map<String, String> headers

	void addHeader(String key, String value) {
		if(headers == null) {
			headers = new HashMap<String, String>()
		}
		headers.put(key, value)
	}
}
