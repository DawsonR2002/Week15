package pet.store.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pet.store.controller.model.PetStoreCustomer;
import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreEmployee;
import pet.store.dao.CustomerDao;
import pet.store.dao.EmployeeDao;
import pet.store.dao.PetStoreDao;
import pet.store.entity.Customer;
import pet.store.entity.Employee;
import pet.store.entity.PetStore;

@Service
public class PetStoreService {

	// @Autowired allows spring to inject the the DAO object
	// into the variable
	@Autowired
	// instance variable
	private PetStoreDao petStoreDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private CustomerDao customerDao;

	@Transactional(readOnly = false)
	public PetStoreEmployee saveEmployee(Long petStoreId, PetStoreEmployee petStoreEmployee) {
		PetStore petStore = findPetStoreById(petStoreId);
		Employee existingEmployee = findOrCreateEmployee(petStoreEmployee.getEmployeeId(), petStoreId);
		copyEmployeeFields(existingEmployee, petStoreEmployee);
		existingEmployee.setPetStore(petStore);
		petStore.getEmployees().add(existingEmployee);
		Employee savedEmployee = employeeDao.save(existingEmployee);
		return mapEmployeeToPetStoreEmployee(savedEmployee);
	}

	private Employee findEmployeeById(Long petStoreId, Long employeeId) {
		// Use the employeeDAO method findById() to return the Employee object
		Employee employee = employeeDao.findById(employeeId)
				.orElseThrow(() -> new NoSuchElementException("Employee not found"));

		// Check if the pet store ID in the Employee object's matches the given pet
		// store ID
		if (!petStoreId.equals(employee.getPetStore().getPetStoreId())) {
			throw new IllegalArgumentException("Employee does not belong to the specified Pet Store");
		}

		// If everything is OK, return the Employee object
		return employee;
	}

	 private Employee findOrCreateEmployee(Long employeeId, Long petStoreId) {
	        // If the employee ID is null, return a new Employee object
	        if (employeeId == null) {
	            return new Employee();
	        }

	        // If the employee ID is not null, call the findEmployeeById() method
	        return findEmployeeById(petStoreId, employeeId);
	    }
	
	    private void copyEmployeeFields(Employee sendToThisEmployee, PetStoreEmployee getFromThisEmployee) {
	        sendToThisEmployee.setEmployeeFirstName(getFromThisEmployee.getEmployeeFirstName());
	        sendToThisEmployee.setEmployeeLastName(getFromThisEmployee.getEmployeeLastName());
	        sendToThisEmployee.setEmployeeId(getFromThisEmployee.getEmployeeId());
	        sendToThisEmployee.setEmployeeJobTitle(getFromThisEmployee.getEmployeeJobTitle());
	        sendToThisEmployee.setEmployeePhone(getFromThisEmployee.getEmployeePhone());
	    }
	    
	    private PetStoreEmployee mapEmployeeToPetStoreEmployee(Employee employee) {
	        PetStoreEmployee petStoreEmployee = new PetStoreEmployee();
	        petStoreEmployee.setEmployeeId(employee.getEmployeeId());
	        petStoreEmployee.setEmployeeFirstName(employee.getEmployeeFirstName());
	        petStoreEmployee.setEmployeeLastName(employee.getEmployeeLastName());
	        petStoreEmployee.setEmployeeJobTitle(employee.getEmployeeJobTitle());
	        petStoreEmployee.setEmployeePhone(employee.getEmployeePhone());
	        
	        return petStoreEmployee;
	    }
	    
		 
	    
	@Transactional(readOnly = false)
	  public PetStoreCustomer saveCustomer(Long petStoreId, PetStoreCustomer customer) {
        PetStore petStore = findPetStoreById(petStoreId);
        Customer existingCustomer = findOrCreateCustomer(customer.getCustomerId(), petStoreId);
        existingCustomer.setCustomerEmail(existingCustomer.getCustomerEmail());
        existingCustomer.setCustomerFirstName(existingCustomer.getCustomerFirstName());
        existingCustomer.setCustomerLastName(existingCustomer.getCustomerLastName());
        existingCustomer.setCustomerId(existingCustomer.getCustomerId());
        existingCustomer.setPetStores(existingCustomer.getPetStores());

        // Update the pet store's customer list
        petStore.getCustomers().add(existingCustomer);

        // Save the customer and return the result
        Customer savedCustomer = customerDao.save(existingCustomer);
        return mapCustomerToPetStoreCustomer(savedCustomer);
    }

	
	private Customer findOrCreateCustomer(Long customerId, Long petStoreId) {
		if (customerId == null) {
			return new Customer();
		}

		return findCustomerById(petStoreId, customerId);
	}

	
	private Customer findCustomerById(Long petStoreId, Long customerId) {
	        Customer customer = customerDao.findById(customerId)
	                .orElseThrow(() -> new NoSuchElementException("Customer not found with ID: " + customerId));

	        //petStore.getPetStoreId().equals(petStoreId) may be an error
	        if (customer.getPetStores().stream().noneMatch(petStore -> petStore.getPetStoreId().equals(petStoreId))) {
	            throw new IllegalArgumentException("Customer does not belong to the specified Pet Store");
	        }

	        return customer;
	    }

	private void copyCustomerFields(Customer sendToThisCustomer, PetStoreCustomer getFromThisCustomer) {
		sendToThisCustomer.setCustomerEmail(getFromThisCustomer.getCustomerEmail());
		sendToThisCustomer.setCustomerFirstName(getFromThisCustomer.getCustomerFirstName());
		sendToThisCustomer.setCustomerLastName(getFromThisCustomer.getCustomerLastName());
		sendToThisCustomer.setCustomerId(getFromThisCustomer.getCustomerId());

	}

	
	private PetStoreCustomer mapCustomerToPetStoreCustomer(Customer customer) {
		PetStoreCustomer petStoreCustomer = new PetStoreCustomer();
		petStoreCustomer.setCustomerEmail((customer.getCustomerEmail()));
		petStoreCustomer.setCustomerFirstName(customer.getCustomerFirstName());
		petStoreCustomer.setCustomerLastName(customer.getCustomerLastName());
		petStoreCustomer.setCustomerId(customer.getCustomerId());		
		

		return petStoreCustomer;
	}

	// good to here




	

	public PetStoreData savePetStore(PetStoreData petStoreData) {

		Long petStoreId = petStoreData.getPetStoreId();
		System.out.println(petStoreId);
		PetStore petStore = findOrCreatePetStore(petStoreId);
		copyPetStoreFields(petStore, petStoreData);
		return new PetStoreData(petStoreDao.save(petStore));
	}

	public void copyPetStoreFields(PetStore petStore, PetStoreData petStoreData) {

		// PetStore petStoreContainer = new PetStore();
		petStore.setPetStoreId(petStoreData.getPetStoreId());
		petStore.setPetStoreName(petStoreData.getPetStoreName());
		petStore.setPetStoreAddress(petStoreData.getPetStoreAddress());
		petStore.setPetStoreCity(petStoreData.getPetStoreCity());
		petStore.setPetStoreState(petStoreData.getPetStoreState());
		petStore.setPetStoreZip(petStoreData.getPetStoreZip());
		petStore.setPetStorePhone(petStoreData.getPetStorePhone());

	}

	public PetStore findOrCreatePetStore(Long petStoreId) {
		if (Objects.isNull(petStoreId)) {
			return new PetStore();
		}

		else {
			return findPetStoreById(petStoreId);
		}
	}

	private PetStore findPetStoreById(Long petStoreId) {
		return petStoreDao.findById(petStoreId)
				.orElseThrow(() -> new NoSuchElementException("Pet store with ID={}" + petStoreId + " was not found."));

	}

	// good to here
	 @Transactional(readOnly = true)
	    public PetStoreData getPetStoreById(Long petStoreId) {
	        PetStore petStore = findPetStoreById(petStoreId);
	        return mapPetStoreToPetStoreData(petStore);
	    }
	 
	 public PetStoreData mapPetStoreToPetStoreData(PetStore petStore) {
	        // Implement mapping logic from PetStore to PetStoreData
		 	PetStoreData petStoreData = new PetStoreData();
		 	petStoreData.setPetStoreCity(petStore.getPetStoreCity());
		 	petStoreData.setPetStoreAddress(petStore.getPetStoreAddress());
		 	petStoreData.setPetStoreName(petStore.getPetStoreName());
		 	petStoreData.setPetStoreId(petStore.getPetStoreId());
		 	petStoreData.setPetStorePhone(petStore.getPetStorePhone());
		 	petStoreData.setPetStoreState(petStore.getPetStoreState());
		 	petStoreData.setPetStoreZip(petStore.getPetStoreZip());
		 	
	        return petStoreData;
	    }
	 
	 @Transactional
	    public void deletePetStoreById(Long petStoreId) {
	        PetStore petStore = findPetStoreById(petStoreId);

	        // Remove employees associated with the pet store
	        petStore.getEmployees().forEach(employee -> employeeDao.delete(employee));

	        // Delete the pet store
	        petStoreDao.delete(petStore);
	    }

	 
	 @Transactional(readOnly = true)
	 public List<PetStoreData> getAllPetStores() {
		    List<PetStore> petStores = petStoreDao.findAll();
		    List<PetStoreData> result = new ArrayList<>();

		    for (PetStore petStore : petStores) {
		        PetStoreData petStoreData = mapPetStoreToPetStoreData(petStore);
		        result.add(petStoreData);
		    }

		    return result;
		}

}
