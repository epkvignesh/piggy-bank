package com.piggy.bank.piggybankgroovy

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler

import groovy.transform.CompileStatic

@CompileStatic
class LambdaHandler implements RequestHandler<Map, Response> {


	@Override
	Response handleRequest(Map input, Context context) {
		final Request request = new Request(input, context)
		log(context, "Received request" + request)
		String resourceType = request.pathParameter("resourceType");
		Response response = null;
		String jsonOutput = 'NA';
		ProvisionService provisionService = new ProvisionService();
		try {
			jsonOutput = provisionService.getCOFStatus();
			response =  Response.builder().statusCode(200).body(jsonOutput).build()
		} catch(Exception e) {
			log(context, "Error while processing the request - " + request.resourcePath(), e)
			response =  Response.builder().statusCode(500).body("Error while processing the request.").build()
		}

		if (response) response.addHeader("Access-Control-Allow-Origin", "*")
		log(context, "Response: ${response}")

		return response;
	}


	void log(Context context, String message){
		log(context, message, null)
	}

	void log(Context context, String message, Exception e){
		if(context != null) {
			context.logger.log(message)
			if (e) context.logger.log(getExceptionTrace(e));
		} else {
			println (message)
			if(e) System.err.println(getExceptionTrace(e))
		}
	}

	String getExceptionTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
}
