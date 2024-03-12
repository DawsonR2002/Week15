package pet.store.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import pet.store.controller.model.PetStoreCustomer;
import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreEmployee;
import pet.store.dao.PetStoreDao;
import pet.store.entity.PetStore;
import pet.store.service.PetStoreService;

@RestController
@RequestMapping("/pet_store")
@Slf4j
public class PetStoreController {

    @Autowired
    private PetStoreService petStoreService;

    @Autowired
    private PetStoreDao petStoreDao;

    public PetStoreController(PetStoreService petStoreService, PetStoreDao petStoreDao) {
        this.petStoreService = petStoreService;
        this.petStoreDao = petStoreDao;
    }

    @PostMapping("/{petStoreId}/employee")
    @ResponseStatus(HttpStatus.CREATED)
    public PetStoreEmployee addEmployeeToStore(@PathVariable Long petStoreId, @RequestBody PetStoreEmployee employee) {
        System.out.println("Received request to add employee to pet store " + petStoreId);
        return petStoreService.saveEmployee(petStoreId, employee);
    }

    @GetMapping("/{petStoreId}")
    public PetStoreData getPetStoreById(@PathVariable Long petStoreId) {
        System.out.println("Received request to retrieve pet store by ID: " + petStoreId);
        return petStoreService.getPetStoreById(petStoreId);
    }

    @DeleteMapping("/{petStoreId}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> deletePetStoreById(@PathVariable Long petStoreId) {
        System.out.println("Received request to delete pet store by ID: " + petStoreId);
        petStoreService.deletePetStoreById(petStoreId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Pet store and associated data deleted successfully");
        return response;
    }
    
    @GetMapping("/all")
    @Transactional
    public List<PetStoreData> retrieveAllPetStores() {
        List<PetStoreData> result = new LinkedList<>();
        List<PetStore> petStores = new LinkedList<>();
        result.addAll(getConvertedPetStoreDataList());
        petStores.addAll(petStoreDao.findAll());
        for (PetStore petStore : petStores) {
            PetStoreData psd = new PetStoreData(petStore);

            psd.getCustomers().clear();
            psd.getEmployees().clear();

            result.add(psd);
        }

        return result;
    }

    @Transactional(readOnly = true)
    @GetMapping
    public List<PetStoreData> getConvertedPetStoreDataList() {
        List<PetStore> petStoreList = petStoreDao.findAll();
        List<PetStoreData> petStoreDataList = convertToPetStoreDataList(petStoreList);
        return petStoreDataList;
    }

    private List<PetStoreData> convertToPetStoreDataList(List<PetStore> petStoreList) {
        // Use a traditional loop to convert each PetStore to PetStoreData
        // This avoids using a collector
        List<PetStoreData> petStoreDataList = new java.util.ArrayList<>();

        for (PetStore petStore : petStoreList) {
            petStoreDataList.add(new PetStoreData(petStore)); // Assuming PetStoreData has a constructor that takes a PetStore
        }

        return petStoreDataList;
    }
    
    @PostMapping("/{petStoreId}/customer")
    @ResponseStatus(HttpStatus.CREATED)
    public PetStoreCustomer addCustomerToStore(@PathVariable Long petStoreId, @RequestBody PetStoreCustomer customer) {
        return petStoreService.saveCustomer(petStoreId, customer);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PetStoreData createPetStore(@RequestBody PetStoreData petStoreData) {
        System.out.println("Received POST request to /pet_store with data: " + petStoreData);
        return petStoreService.savePetStore(petStoreData);
    }

    @PutMapping("/{storeId}")
    public PetStoreData modifyPetStore(@PathVariable Long storeId, @RequestBody PetStoreData petStoreData) {
        petStoreData.setPetStoreId(storeId);
        log.info("Updated data using PUT");
        return petStoreService.savePetStore(petStoreData);
    }
}