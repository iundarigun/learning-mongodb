package br.com.devcave.mongo.customer.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
//@AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@JsonCreator}))
public class CustomerRequest {
    private final String name;

    @JsonCreator
    private CustomerRequest(final String name) {
      this.name = name;
    }
}
