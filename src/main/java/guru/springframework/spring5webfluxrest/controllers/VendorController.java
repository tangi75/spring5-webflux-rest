package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(VendorController.BASE_URL)
public class VendorController {

    static final String BASE_URL = "/api/v1/vendors";

    private final VendorRepository vendorRepository;

    public VendorController(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @GetMapping
    Flux<Vendor> list() {
        return vendorRepository.findAll();
    }

    @GetMapping("/{id}")
    Mono<Vendor> getById(@PathVariable String id) {
        return vendorRepository.findById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    Mono<Void> create(@RequestBody Publisher<Vendor> vendorStream) {
        return vendorRepository.saveAll(vendorStream).then();
    }

    @PutMapping("/{id}")
    Mono<Vendor> update(@PathVariable String id, @RequestBody Vendor vendor) {
        vendor.setId(id);
        return vendorRepository.save(vendor);
    }

    @PatchMapping("/{id}")
    Mono<Vendor> patch(@PathVariable String id, @RequestBody Vendor vendor) {

        Vendor foundVendor = vendorRepository.findById(id).block();
        boolean modified = false;

        if (vendor.getFirstName() != null &&
                (foundVendor.getFirstName() == null || !foundVendor.getFirstName().equals(vendor.getFirstName()))) {
            foundVendor.setFirstName(vendor.getFirstName());
            modified = true;
        }
        if (vendor.getLastName() != null &&
                (foundVendor.getLastName() == null || !foundVendor.getLastName().equals(vendor.getLastName()))) {
            foundVendor.setLastName(vendor.getLastName());
            modified = true;
        }
        return modified ? vendorRepository.save(foundVendor) : Mono.just(foundVendor);
    }
}
