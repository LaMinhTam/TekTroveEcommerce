package com.tektrove.tektrovecustomer.customer;

import com.tektrove.tektrovecustomer.security.CustomerUserDetails;
import com.tektrove.tektrovecustomer.security.oauth.CustomerOAuth2User;
import com.tektrove.tektrovecustomer.setting.SettingService;
import com.tektrove.tektrovecustomer.utils.AuthenticationUtil;
import com.tektrove.tektrovecustomer.utils.MailSenderHelper;
import com.tektrovecommon.entity.Customer;
import com.tektrovecommon.entity.setting.Country;
import com.tektrovecommon.entity.setting.Setting;
import com.tektrovecommon.entity.setting.SettingBag;
import com.tektrovecommon.entity.setting.SettingCategory;
import com.tektrovecommon.exception.CustomerNotFoundException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        String toAddress = customer.getEmail();
        String subject = emailSettings.getValue("CUSTOMER_VERIFY_SUBJECT");
        String content = emailSettings.getValue("CUSTOMER_VERIFY_CONTENT");

        content = content.replace("[[name]]", customer.getFullName());
        String verifyUrl = MailSenderHelper.getSiteUrl(request) + "/verify?code=" + customer.getActivationCode();
        content = content.replace("[[URL]]", verifyUrl);

        sendEmail(emailSettings, toAddress, subject, content);
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

    @GetMapping("/account_details")
    public String showAccountDetails(Model model, HttpServletRequest request) throws CustomerNotFoundException {
        String email = AuthenticationUtil.getEmailOfAuthenticatedCustomer(request);
        Customer customer = customerService.getCustomerByEmail(email);
        List<Country> countries = customerService.getCountries();
        model.addAttribute("countries", countries);
        model.addAttribute("customer", customer);
        return "customer/account_details";
    }


    @PostMapping("/update_account_details")
    public String updateCustomer(Customer customer, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        customerService.update(customer);
        redirectAttributes.addFlashAttribute("message", "Your account details have been updatedss!");
        updateNameForAuthenticatedCustomer(customer, request);
        return "redirect:/account_details";
    }

    private void updateNameForAuthenticatedCustomer(Customer customer, HttpServletRequest request) {
        Object principal = request.getUserPrincipal();
        if (principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken) {
            CustomerUserDetails userDetails = getCustomerUserDetails(principal);
            Customer authenticatedCustomer = userDetails.getCustomer();
            authenticatedCustomer.setFirstName(customer.getFirstName());
            authenticatedCustomer.setLastName(customer.getLastName());
        } else if (principal instanceof OAuth2AuthenticationToken oauth2Token) {
            CustomerOAuth2User oauth2User = (CustomerOAuth2User) oauth2Token.getPrincipal();
            oauth2User.setFullName(customer.getFullName());
        }
    }

    private CustomerUserDetails getCustomerUserDetails(Object principal) {
        CustomerUserDetails customerUserDetails = null;
        if (principal instanceof UsernamePasswordAuthenticationToken token) {
            customerUserDetails = (CustomerUserDetails) token.getPrincipal();
        } else if (principal instanceof RememberMeAuthenticationToken token) {
            customerUserDetails = (CustomerUserDetails) token.getPrincipal();
        }
        return customerUserDetails;
    }

    @GetMapping("/show_forgot")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("pageTitle", "Forgot Password");
        return "authentication/forgot_password/forgot_password_form";
    }

    @PostMapping("/forgot_password")
    public String processForgotPassword(String email, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.updateResetPasswordToken(email);
            sendForgotPasswordEmail(request, customer);
            redirectAttributes.addFlashAttribute("message", "We have sent you an email to reset your password.");
        } catch (CustomerNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            redirectAttributes.addFlashAttribute("error", "Error while sending email. Please try again later.");
        }
        return "redirect:/show_forgot";
    }

    private void sendForgotPasswordEmail(HttpServletRequest request, Customer customer) throws MessagingException, UnsupportedEncodingException {
        List<Setting> settings = settingService.getSettings(SettingCategory.MAIL_SERVER, SettingCategory.MAIL_TEMPLATES);
        SettingBag emailSettings = new SettingBag(settings);

        String toAddress = customer.getEmail();
        String subject = "Here's the link to reset your password";
        String resetPasswordUrl = MailSenderHelper.getSiteUrl(request) + "/reset_password?code=" + customer.getResetPasswordToken();
        String content = "<p>Hello,</p>" +
                "<p>You have requested to reset your password.</p>" +
                "<p>Click the link below to change your password:</p>" +
                "<p><a href=\"" + resetPasswordUrl + "\">Change my password</a></p>" +
                "<br>" +
                "<p>Ignore this email if you do remember your password, or you have not made the request.</p>" +
                "<p>Thank you</p>";

        sendEmail(emailSettings, toAddress, subject, content);
    }

    private void sendEmail(SettingBag emailSettings, String toAddress, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        JavaMailSenderImpl javaMailSender = MailSenderHelper.prepareMailSender(emailSettings);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);

        mimeMessageHelper.setFrom(emailSettings.getValue("MAIL_FROM"), emailSettings.getValue("MAIL_SENDER_NAME"));
        mimeMessageHelper.setTo(toAddress);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(content, true);

        javaMailSender.send(message);
    }

    @GetMapping("/reset_password")
    public String showResetPasswordForm(String code, Model model) {
        try {
            Customer customer = customerService.getCustomerByResetPasswordToken(code);
            model.addAttribute("token", customer.getResetPasswordToken());
        } catch (CustomerNotFoundException e) {
            model.addAttribute("pageTitle", "Reset Password");
            model.addAttribute("message", "Invalid Token");
            return "message";
        }
        return "authentication/forgot_password/reset_password_form";
    }

    @PostMapping("/reset_password")
    public String processResetPassword(String token, String password, Model model) {
        try {
            customerService.resetPassword(token, password);
            model.addAttribute("pageTitle", "Reset Password");
            model.addAttribute("title", "Reset Your Password");
            model.addAttribute("message", "You have successfully changed your password.");
        } catch (CustomerNotFoundException e) {
            model.addAttribute("pageTitle", "Reset Password");
            model.addAttribute("message", e.getMessage());
        }
        return "message";
    }
}
