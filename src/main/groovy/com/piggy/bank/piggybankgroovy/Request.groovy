package com.piggy.bank.piggybankgroovy

import com.amazonaws.services.lambda.runtime.Context

import groovy.transform.ToString

@ToString(includePackage = false)
class Request {
	private final Map input
	private final Context context

	Request(final Map input, final Context context) {
		this.input = input
		this.context = context
	}

	String requestId() {
		context?.awsRequestId
	}

	String resourcePath() {
		input?.resource ?: 'unknown'
	}

	String httpMethod() {
		input?.httpMethod ?: 'unknown'
	}

	String queryString(String name) {
		String value = input?.queryStringParameters?."${name}"
		value ? URLDecoder.decode(value?.trim(), "UTF-8") : ''
	}

	String pathParameter(String name) {
		String value = input?.pathParameters?."${name}"
		value ? URLDecoder.decode(value?.trim(), "UTF-8") : ''
	}

	String requestBody() {
		input?.body
	}
}
