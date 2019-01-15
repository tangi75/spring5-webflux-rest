package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.BDDMockito.given;

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
                .willReturn(Flux.just(VENDOR_1,VENDOR_2));

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
}
