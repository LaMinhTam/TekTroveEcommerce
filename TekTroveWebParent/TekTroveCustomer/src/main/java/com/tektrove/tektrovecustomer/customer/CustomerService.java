package com.tektrove.tektrovecustomer.customer;

import com.tektrove.tektrovecustomer.setting.CountryRepository;
import com.tektrovecommon.entity.Customer;
import com.tektrovecommon.entity.setting.Country;
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
}
