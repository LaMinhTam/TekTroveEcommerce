package com.tektrove.tektrovecustomer.setting;

import com.tektrovecommon.entity.setting.Setting;
import com.tektrovecommon.entity.setting.SettingCategory;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;


@Component
public class SettingFilter implements Filter {
    private final SettingService settingService;

    public SettingFilter(SettingService settingService) {
        this.settingService = settingService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String url = request.getRequestURL().toString();
        if (url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".png") || url.endsWith(".jpg")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        List<Setting> settings = settingService.getSettings(SettingCategory.GENERAL, SettingCategory.CURRENCY);
        settings.forEach(setting -> {
//            System.out.println("setting = " + setting);
            request.setAttribute(setting.getKey(), setting.getValue());
        });
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
