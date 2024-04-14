package com.tektrove.tektrovecustomer.customer;

import com.tektrove.tektrovecustomer.setting.CountryRepository;
import com.tektrovecommon.entity.AuthenticationType;
import com.tektrovecommon.entity.Customer;
import com.tektrovecommon.entity.setting.Country;
import com.tektrovecommon.exception.CustomerNotFoundException;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CountryRepository countryRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, CountryRepository countryRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.countryRepository = countryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Country> getCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(String email) {
        return customerRepository.findByEmail(email).isEmpty();
    }

    public Customer getCustomerByEmail(String email) throws CustomerNotFoundException {
        return customerRepository.findByEmail(email).orElseThrow(() -> new CustomerNotFoundException("Could not find any customer with email " + email));
    }

    public void registerCustomer(Customer customer) {
        encodePassword(customer);
        customer.setEnabled(false);
        customer.setCreatedTime(new Date());
        String activationCode = RandomString.make(64);
        customer.setActivationCode(activationCode);
        customerRepository.save(customer);
    }

    private void encodePassword(Customer customer) {
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
    }

    public boolean verify(String verification) {
        Optional<Customer> customer = customerRepository.findByActivationCode(verification);
        if (customer.isPresent()) {
            customerRepository.enabledCustomer(customer.get().getId());
            return true;
        }
        return false;
    }

    public void updateAuthentication(Customer customer, AuthenticationType authenticationType) {
        customerRepository.updateAuthenticationType(customer.getId(), authenticationType);
    }

    public void addNewCustomerUponOAuthLogin(String name, String email, String countryCode, AuthenticationType authenticationType) {
        Customer customer = new Customer();
        setNameForCustomerUponOAuthLogin(name, customer);
        customer.setEmail(email);
        customer.setAuthenticationType(authenticationType);
        customer.setEnabled(true);
        customer.setCreatedTime(new Date());
        customer.setPassword("");
        customer.setAddressLine1("");
        customer.setCity("");
        customer.setState("");
        customer.setPostalCode("");
        customer.setPhoneNumber("");
        customer.setCountry(countryRepository.findByCode(countryCode));
        customerRepository.save(customer);
    }

    public void setNameForCustomerUponOAuthLogin(String name, Customer customer) {
        String[] names = name.split(" ");
        if (names.length == 1) {
            customer.setFirstName(names[0]);
            customer.setLastName("");
        } else {
            String firstName = names[0];
            customer.setFirstName(firstName);
            customer.setLastName(name.replaceFirst(firstName + " ", ""));
        }
    }

    public void update(Customer customer) {
        Customer customerInDB = customerRepository.findById(customer.getId()).get();
        if (customerInDB.getAuthenticationType() == AuthenticationType.DATABASE) {
            if (customer.getPassword() == null || customer.getPassword().isEmpty()) {
                customer.setPassword(customerInDB.getPassword());
            } else {
                String encodePassword = passwordEncoder.encode(customer.getPassword());
                customer.setPassword(encodePassword);
            }
        } else {
            customer.setPassword(customerInDB.getPassword());
        }

        customer.setEnabled(customerInDB.isEnabled());
        customer.setCreatedTime(customerInDB.getCreatedTime());
        customer.setActivationCode(customerInDB.getActivationCode());
        customer.setAuthenticationType(customerInDB.getAuthenticationType());
        customer.setResetPasswordToken(customerInDB.getResetPasswordToken());
        customerRepository.save(customer);
    }

    public Customer updateResetPasswordToken(String email) throws CustomerNotFoundException {
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new CustomerNotFoundException("Could not find any customer with email " + email));
        String token = RandomString.make(30);
        customer.setResetPasswordToken(token);
        return customerRepository.save(customer);
    }

    public Customer getCustomerByResetPasswordToken(String token) throws CustomerNotFoundException {
        return customerRepository.findByResetPasswordToken(token).orElseThrow(() -> new CustomerNotFoundException("Invalid token"));
    }

    public void resetPassword(String token, String password) throws CustomerNotFoundException {
        Customer customer = customerRepository.findByResetPasswordToken(token).orElseThrow(() -> new CustomerNotFoundException("No customer found: invalid token "));
        customer.setPassword(passwordEncoder.encode(password));
        customer.setResetPasswordToken(null);
        customerRepository.save(customer);
    }
}
