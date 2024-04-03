package com.tektrove.tektroveadmin.customer;

import com.tektrove.tektroveadmin.setting.country.CountryRepository;
import com.tektrovecommon.entity.Customer;
import com.tektrovecommon.entity.setting.Country;
import com.tektrovecommon.exception.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerService {
    public static final int CUSTOMER_PER_PAGE = 10;
    private final CustomerRepository customerRepository;
    private final CountryRepository countryRepository;
    private final PasswordEncoder passwordEncoder;
    public CustomerService(CustomerRepository customerRepository, CountryRepository countryRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.countryRepository = countryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<Customer> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, CUSTOMER_PER_PAGE, sort);
        if (keyword != null) {
            return customerRepository.findAll(keyword, pageable);
        }
        return customerRepository.findAll(pageable);
    }

    public void updateCustomerEnabledStatus(Integer id, boolean enabled) {
        customerRepository.updateEnabledStatus(id, enabled);
    }

    public Customer get(int id) throws CustomerNotFoundException {
        return customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException("Customer with ID: " + id + " is not found"));
    }

    public void save(Customer customer) {
        Customer customerInDB = customerRepository.findById(customer.getId()).get();
        if (customer.getPassword() == null || customer.getPassword().isEmpty()) {
            customer.setPassword(customerInDB.getPassword());
        } else {
            String encodePassword = passwordEncoder.encode(customer.getPassword());
            customer.setPassword(encodePassword);
        }

        customer.setEnabled(customerInDB.isEnabled());
        customer.setCreatedTime(customerInDB.getCreatedTime());
        customer.setActivationCode(customerInDB.getActivationCode());

        customerRepository.save(customer);
    }

    public void delete(int id) throws CustomerNotFoundException {
        Long count = customerRepository.countById(id);
        if (count == null || count == 0) {
            throw new CustomerNotFoundException("Could not find any customer with ID: " + id);
        }
        customerRepository.deleteById(id);
    }

    public List<Country> listAllCountry() {
        return countryRepository.findAll();
    }

    public boolean isEmailUnique(Integer id, String email) {
        Customer existCustomer = customerRepository.findByEmail(email);

        return existCustomer == null || existCustomer.getId() == id;
    }
}
