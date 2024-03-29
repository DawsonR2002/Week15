package pet.store.controller.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import pet.store.entity.Customer;

@Data
@NoArgsConstructor
//JPA requires recursion
public class PetStoreCustomer {
	private Long customerId;
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;
    
    PetStoreCustomer(Customer customer){
    	customerId = customer.getCustomerId();
    	customerFirstName = customer.getCustomerFirstName();
    	customerLastName = customer.getCustomerLastName();
    	customerEmail = customer.getCustomerEmail();
    }
}
