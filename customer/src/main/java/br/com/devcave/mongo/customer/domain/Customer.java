package br.com.devcave.mongo.customer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;


@Getter
@Document("customers")
public class Customer {

    @Id
    @JsonIgnore
    private final ObjectId id;

    private final String name;

    public static Customer createCustomer(final String name) {
        return new Customer(null, name);
    }

    private Customer(final ObjectId id, final String name) {
        this.id = id;
        this.name = name;
    }

    @JsonProperty("id")
    public String getIdAsString(){
        return this.id.toString();
    }
}
