package com.cadre.server.core.web;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.ws.rs.core.MultivaluedMap;

public class RequestWrapper extends HttpServletRequestWrapper {

    private MultivaluedMap<String, String> form;

    public RequestWrapper(HttpServletRequest request, MultivaluedMap<String, String> form)
    { super(request); this.form = form; }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value == null)
        { value = form.getFirst(name); }
        return value;
    }
    
    @Override
    public Enumeration<String> getParameterNames(){
		return Collections.enumeration(form.keySet());
    }

	public MultivaluedMap<String, String> getForm() {
		return form;
	}
    
    public String getHeader(String name) {
        String header = super.getHeader(name);
        return (header != null) ? header : super.getParameter(name); // Note: you can't use getParameterValues() here.
    }

    public Enumeration getHeaderNames() {
        List<String> names = Collections.list(super.getHeaderNames());
        names.addAll(Collections.list(super.getParameterNames()));
        return Collections.enumeration(names);
    }
    
    
}