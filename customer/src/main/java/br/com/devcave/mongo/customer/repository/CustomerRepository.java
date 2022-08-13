package br.com.devcave.mongo.customer.repository;

import br.com.devcave.mongo.customer.domain.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<Customer, String> {
}

