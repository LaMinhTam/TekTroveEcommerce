package com.tektrove.tektroveadmin.paging;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PagingAndSortingArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(PagingAndSortingParam.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String sortField = webRequest.getParameter("sortField");
        String sortDir = webRequest.getParameter("sortDir");
        String keyword = webRequest.getParameter("keyword");

        mavContainer.addAttribute("sortField", sortField);
        mavContainer.addAttribute("sortDir", sortDir);
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        mavContainer.addAttribute("reverseSortDir", reverseSortDir);
        mavContainer.addAttribute("keyword", keyword);

        PagingAndSortingParam parameterAnnotation = parameter.getParameterAnnotation(PagingAndSortingParam.class);

        return new PagingAndSortingHelper(mavContainer, parameterAnnotation.moduleName(), sortField, sortDir, keyword);
    }

}
