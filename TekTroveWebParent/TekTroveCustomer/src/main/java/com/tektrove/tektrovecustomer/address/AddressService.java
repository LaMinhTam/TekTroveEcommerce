package com.tektrove.tektrovecustomer.address;

import com.tektrovecommon.entity.customer.Address;
import com.tektrovecommon.entity.customer.Customer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AddressService {
    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<Address> listAll(Integer customerId) {
        return addressRepository.findByCustomer_Id(customerId);
    }

    public Address get(Integer customerId, Integer addressId) {
        return addressRepository.findByCustomer_IdAndId(customerId, addressId);
    }

    public void save(Address address, Customer customer) {
        if (address.isDefaultAddress()) {
            // If the new address is set as default, set other addresses for the same customer to false
            addressRepository.updateDefaultAddress(customer.getId(), address.getId());
        }
        address.setCustomer(customer);
        addressRepository.save(address);
    }

    public void delete(Integer customerId, Integer addressId) {
        addressRepository.DeleteByCustomer_IdAndId(customerId, addressId);
    }

    public void setDefaultAddress(Integer customerId, Integer addressId) {
        addressRepository.updateDefaultAddress(customerId, addressId);
    }
}
