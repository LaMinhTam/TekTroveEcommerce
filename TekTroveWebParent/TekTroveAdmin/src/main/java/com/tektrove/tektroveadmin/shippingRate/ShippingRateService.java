package com.tektrove.tektroveadmin.shippingRate;

import com.tektrove.tektroveadmin.paging.PagingAndSortingHelper;
import com.tektrove.tektroveadmin.setting.country.CountryRepository;
import com.tektrovecommon.entity.ShippingRate;
import com.tektrovecommon.entity.setting.Country;
import com.tektrovecommon.exception.ShippingRateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ShippingRateService {
    private static final int SHIPPING_RATE_PER_PAGE = 10;
    private final ShippingRateRepository shippingRateRepository;
    private final CountryRepository countryRepository;

    public ShippingRateService(ShippingRateRepository shippingRateRepository, CountryRepository countryRepository) {
        this.shippingRateRepository = shippingRateRepository;
        this.countryRepository = countryRepository;
    }

    public List<ShippingRate> listAll() {
        return shippingRateRepository.findAll();
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listByPage(pageNum, SHIPPING_RATE_PER_PAGE, shippingRateRepository);
    }

    public void save(ShippingRate rateInForm) throws ShippingRateException {
        ShippingRate rateInDB = shippingRateRepository.findByCountry_IdAndState(
                rateInForm.getCountry().getId(), rateInForm.getState());

        boolean isNewRate = rateInForm.getId() == null;
        boolean isDifferentExistingRate = rateInDB != null && !rateInDB.equals(rateInForm);

        if ((isNewRate && rateInDB != null) || isDifferentExistingRate) {
            throw new ShippingRateException("There's already a rate for the destination "
                    + rateInForm.getCountry().getName() + ", " + rateInForm.getState());
        }

        shippingRateRepository.save(rateInForm);
    }


    public ShippingRate get(Integer id) throws ShippingRateException {
        return shippingRateRepository.findById(id).orElseThrow(() -> new ShippingRateException("Shipping rate not found"));
    }

    public void delete(Integer id) throws ShippingRateException {
        ShippingRate shippingRate = shippingRateRepository.findById(id).orElseThrow(() -> new ShippingRateException("Shipping rate not found"));
        shippingRateRepository.delete(shippingRate);
    }

    public void updateCODSupport(Integer id, boolean codSupport) throws ShippingRateException {
        ShippingRate shippingRate = shippingRateRepository.findById(id).orElseThrow(() -> new ShippingRateException("Shipping rate not found"));
        shippingRate.setCodSupported(codSupport);
        shippingRateRepository.save(shippingRate);
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAll();
    }
}
