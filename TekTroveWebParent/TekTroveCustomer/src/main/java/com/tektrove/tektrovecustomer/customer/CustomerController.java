package com.tektrove.tektrovecustomer.customer;

import com.tektrove.tektrovecustomer.setting.SettingService;
import com.tektrove.tektrovecustomer.utils.MailSenderHelper;
import com.tektrovecommon.entity.Customer;
import com.tektrovecommon.entity.setting.Country;
import com.tektrovecommon.entity.setting.Setting;
import com.tektrovecommon.entity.setting.SettingBag;
import com.tektrovecommon.entity.setting.SettingCategory;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
public class CustomerController {
    private final CustomerService customerService;
    private final SettingService settingService;

    public CustomerController(CustomerService customerService, SettingService settingService) {
        this.customerService = customerService;
        this.settingService = settingService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        List<Country> countries = customerService.getCountries();
        model.addAttribute("countries", countries);
        model.addAttribute("pageTitle", "Customer Registration");
        model.addAttribute("customer", new Customer());

        return "authentication/register/register_form";
    }

    @PostMapping("/create_customer")
    public String createCustomer(Customer customer, Model model, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        customerService.registerCustomer(customer);
        sendVerificationEmail(request, customer);
        model.addAttribute("pageTitle", "Registration Successful!");
        return "authentication/register/register_success";
    }

    private void sendVerificationEmail(HttpServletRequest request, Customer customer) throws MessagingException, UnsupportedEncodingException {
        List<Setting> settings = settingService.getSettings(SettingCategory.MAIL_SERVER, SettingCategory.MAIL_TEMPLATES);
        SettingBag emailSettings = new SettingBag(settings);
        JavaMailSenderImpl javaMailSender = MailSenderHelper.prepareMailSender(emailSettings);

        String toAddress = customer.getEmail();
        String subject = emailSettings.getValue("CUSTOMER_VERIFY_SUBJECT");
        String content = emailSettings.getValue("CUSTOMER_VERIFY_CONTENT");

        content = content.replace("[[name]]", customer.getFullName());
        String verifyUrl = MailSenderHelper.getSiteUrl(request) + "/verify?code=" + customer.getActivationCode();
        content = content.replace("[[URL]]", verifyUrl);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);

        mimeMessageHelper.setFrom(emailSettings.getValue("MAIL_FROM"), emailSettings.getValue("MAIL_SENDER_NAME"));
        mimeMessageHelper.setTo(toAddress);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(content, true);

        javaMailSender.send(message);
    }

    @GetMapping("verify")
    public String verifyAccount(String code, Model model) {
        boolean verified = customerService.verify(code);
        model.addAttribute("isSuccess", verified);
        if (verified) {
            model.addAttribute("pageTitle", "Verification Successful!");
        } else {
            model.addAttribute("pageTitle", "Verification Failed!");
        }
        return "authentication/register/verify_result";
    }
}
