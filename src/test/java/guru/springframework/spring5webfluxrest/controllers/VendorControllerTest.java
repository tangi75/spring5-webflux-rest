package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class VendorControllerTest {

    WebTestClient webTestClient;

    @Mock
    VendorRepository vendorRepository;

    @InjectMocks
    VendorController vendorController;

    private final Vendor VENDOR_1 = Vendor.builder().firstName("Fred").firstName("Flintstone").build();
    private final Vendor VENDOR_2 = Vendor.builder().firstName("Barney").firstName("Rubble").build();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        webTestClient = WebTestClient.bindToController(vendorController).build();
    }

    @Test
    public void list() {
        given(vendorRepository.findAll())
                .willReturn(Flux.just(VENDOR_1, VENDOR_2));

        webTestClient.get()
                .uri(VendorController.BASE_URL)
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(2);
    }

    @Test
    public void getById() {
        given(vendorRepository.findById("someid"))
                .willReturn(Mono.just(VENDOR_1));

        webTestClient.get()
                .uri(VendorController.BASE_URL + "/someid")
                .exchange()
                .expectBody(Vendor.class);
    }

    @Test
    public void testCreateVendor() {
        given(vendorRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Vendor.builder().build()));

        Mono<Vendor> vendorToSaveMono = Mono.just(Vendor.builder().firstName("First Name")
                .lastName("Last Name").build());

        webTestClient.post()
                .uri(VendorController.BASE_URL)
                .body(vendorToSaveMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void testUpdateVendor() {

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorMonoToUpdate = Mono.just(Vendor.builder().build());

        webTestClient.put()
                .uri(VendorController.BASE_URL + "/someid")
                .body(vendorMonoToUpdate, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

    }

    @Test
    public void testPatchVendorWithChanges() {

        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(VENDOR_1));

        Vendor savedVendor = Vendor.builder().firstName("Jim").firstName("Flintstone").build();

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(savedVendor));

        Mono<Vendor> vendorMonoToUpdate = Mono.just(Vendor.builder().firstName("Jim").build());

        webTestClient.patch()
                .uri(VendorController.BASE_URL + "/someid")
                .body(vendorMonoToUpdate, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(vendorRepository).save(any());
    }

    @Test
    public void testPatchVendorWithoutChanges() {

        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(VENDOR_1));

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorMonoToUpdate = Mono.just(VENDOR_1);

        webTestClient.patch()
                .uri(VendorController.BASE_URL + "/someid")
                .body(vendorMonoToUpdate, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(vendorRepository, never()).save(any());
    }
}
