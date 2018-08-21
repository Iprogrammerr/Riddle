package com.iprogrammerr.riddle.router;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iprogrammerr.riddle.exception.request.ParsingUrlException;
import com.iprogrammerr.riddle.exception.request.RequestParameterException;
import com.iprogrammerr.riddle.exception.request.WrongRequestBodyException;
import com.iprogrammerr.riddle.exception.router.NotResolvedRouteException;
import com.iprogrammerr.riddle.exception.validation.JsonParsingException;
import com.iprogrammerr.riddle.service.json.JsonService;
import com.iprogrammerr.riddle.util.StringUtil;

public abstract class Route {

    private String mainPath;
    private JsonService jsonService;

    public Route(String mainPath, JsonService jsonService) {
	this.mainPath = mainPath;
	this.jsonService = jsonService;
    }

    public void resolveGetRequest(String path, HttpServletRequest request, HttpServletResponse response) {
	throw new NotResolvedRouteException();
    }

    public void resolvePostRequest(String path, HttpServletRequest request, HttpServletResponse response) {
	throw new NotResolvedRouteException();
    }

    public void resolvePutRequest(String path, HttpServletRequest request, HttpServletResponse response) {
	throw new NotResolvedRouteException();
    }

    public void resolveDeleteRequest(String path, HttpServletRequest request, HttpServletResponse response) {
	throw new NotResolvedRouteException();
    }

    public String getMainPath() {
	return mainPath;
    }

    public <T> T getUrlLastVariable(Class<T> clazz, String url) {
	try {
	    String[] segmentedUrl = url.split("/");
	    if (segmentedUrl.length < 1) {
		throw new ParsingUrlException("Required variable not presented in url.");
	    }
	    String valueToCast = segmentedUrl[segmentedUrl.length - 1];
	    return castUrlVariable(clazz, valueToCast);
	} catch (ClassCastException exception) {
	    exception.printStackTrace();
	    throw new ParsingUrlException("Required url variable is of wrong type");
	}
    }

    private <T> T castUrlVariable(Class<T> clazz, String urlVariable) {
	if (clazz.isAssignableFrom(Long.class)) {
	    return clazz.cast(Long.parseLong(urlVariable));
	}
	return clazz.cast(urlVariable);
    }

    protected <T> T getBody(Class<T> clazz, HttpServletRequest request) {
	try {
	    T object = jsonService.deserialize(clazz, request.getInputStream());
	    return object;
	} catch (IOException exception) {
	    exception.printStackTrace();
	    throw new WrongRequestBodyException();
	}
    }

    protected <T> void setBody(T object, HttpServletResponse response) {
	try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()))) {
	    String body = jsonService.serialize(object);
	    writer.write(body);
	    response.setContentLength(body.getBytes().length);
	} catch (IOException exception) {
	    exception.printStackTrace();
	    throw new JsonParsingException(exception);
	}
    }

    protected <T> T getParameter(Class<T> clazz, String parameterKey, HttpServletRequest request) {
	try {
	    String parameterValue = request.getParameter(parameterKey);
	    if (StringUtil.isNullOrEmpty(parameterValue)) {
		throw new RequestParameterException(parameterKey + " is required.");
	    }
	    return clazz.cast(parameterValue);
	} catch (ClassCastException exception) {
	    exception.printStackTrace();
	    throw new RequestParameterException(parameterKey + " is invalid");
	}
    }

}
