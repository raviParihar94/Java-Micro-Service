package dev.beelive.com.product_service;

import dev.beelive.com.product_service.dto.ProductRequest;
import dev.beelive.com.product_service.dto.ProductResponse;
import dev.beelive.com.product_service.model.Product;
import dev.beelive.com.product_service.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

import com.fasterxml.jackson.databind.ObjectMapper;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	ProductRepository productRepository;

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.data.mongodb.uri",mongoDBContainer::getReplicaSetUrl);
	}

	static {
		mongoDBContainer.start();
	}

	@Test
	void shouldCreateProduct()throws Exception {
		ProductRequest productRequest = getProductRequest();
		String  productRequestStr = objectMapper.writeValueAsString(productRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productRequestStr)
		).andExpect(status().isCreated());

		Assertions.assertEquals(1,productRepository.findAll().size());
	}

	private ProductRequest getProductRequest(){

		return ProductRequest.builder()
				.productName("samsung s3")
				.description("samsung s3")
				.price(BigDecimal.valueOf(870))
				.build();
	}

	@Test
	void getAllProductTest() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
		).andExpect(status().isOk());
		List<ProductResponse> allProduct = getAllProduct();
		Assertions.assertEquals(1, allProduct.size());
		Assertions.assertEquals("1", allProduct.getFirst().getProductID());
		Assertions.assertEquals("Product A", allProduct.getFirst().getProductName());
		Assertions.assertEquals("Description A", allProduct.getFirst().getDescription());
		Assertions.assertEquals(new BigDecimal("10.0"), allProduct.getFirst().getPrice());
	}

	private List<ProductResponse> getAllProduct() {
		Product product = new Product("1", "Product A", "Description A",  new BigDecimal("10.0"));
		productRepository.save(product);
		List<Product> allProduct = productRepository.findAll();

		return allProduct.stream().map(this::mapToProductResponseTest).toList();
	}

	// Helper method to map Product to ProductResponse
	private ProductResponse mapToProductResponseTest(Product product) {
		return ProductResponse.builder()
				.productID(product.getProductID())
				.productName(product.getProductName())
				.description(product.getDescription())
				.price(product.getPrice())
				.build();
	}

}
