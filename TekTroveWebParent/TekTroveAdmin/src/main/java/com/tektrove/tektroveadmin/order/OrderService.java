package com.tektrove.tektroveadmin.order;

import com.tektrove.tektroveadmin.paging.PagingAndSortingHelper;
import com.tektrove.tektroveadmin.setting.country.CountryRepository;
import com.tektrovecommon.entity.order.Order;
import com.tektrovecommon.entity.setting.Country;
import com.tektrovecommon.exception.OrderNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private static final int ORDERS_PER_PAGE = 10;
    private final OrderRepository orderRepository;
    private final CountryRepository countryRepository;

    public OrderService(OrderRepository orderRepository, CountryRepository countryRepository) {
        this.orderRepository = orderRepository;
        this.countryRepository = countryRepository;
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        String sortField = helper.getSortField();
        String sortDir = helper.getSortDir();
        String keyword = helper.getKeyword();
        Sort sort = null;
        if ("destination".equals(sortField)) {
            sort = Sort.by("country").and(Sort.by("state")).and(Sort.by("city"));
        } else {
            sort = Sort.by(sortField);
        }

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);
        Page<Order> page = null;
        if (keyword != null) {
            page = orderRepository.findAll(keyword, pageable);
        } else {
            page = orderRepository.findAll(pageable);
        }

        helper.updateModelAttributes(pageNum, page);
    }

    public Order findById(int id) throws OrderNotFoundException {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("Could not find order with id: " + id));
    }

    public Order save(Order order) {
        Order orderInDB = orderRepository.findById(order.getId()).get();
        order.setOrderTime(orderInDB.getOrderTime());
        order.setCustomer(orderInDB.getCustomer());
        return orderRepository.save(order);
    }

    public void delete(int id) throws OrderNotFoundException {
        Long count = orderRepository.countById(id);
        if (count == null || count == 0) {
            throw new OrderNotFoundException("Could not find order with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    public List<Country> listCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }
}
