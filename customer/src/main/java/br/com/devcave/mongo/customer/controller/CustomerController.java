package br.com.devcave.mongo.customer.controller;

import br.com.devcave.mongo.customer.domain.Customer;
import br.com.devcave.mongo.customer.domain.CustomerRequest;
import br.com.devcave.mongo.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("customers")
public class CustomerController {

    private final CustomerRepository customerRepository;

    @PostMapping
    public String create(@RequestBody final CustomerRequest customerRequest) {
        final Customer customer = Customer.createCustomer(customerRequest.getName());
        customerRepository.save(customer);
        return "";
    }

    @GetMapping
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }
    @GetMapping("{id}")
    public Customer findById(@PathVariable final String id) {
        return customerRepository.findById(id).orElse(null);
    }
}
