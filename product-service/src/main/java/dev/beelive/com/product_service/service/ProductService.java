package dev.beelive.com.product_service.service;

import dev.beelive.com.product_service.dto.ProductRequest;
import dev.beelive.com.product_service.dto.ProductResponse;
import dev.beelive.com.product_service.model.Product;
import dev.beelive.com.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository ;

    public void createProduct(ProductRequest productRequest){
        Product product = Product.builder()
                .productName(productRequest.getProductName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);
        log.info("Product {} is saved" , product.getProductID());
    }

    public List<ProductResponse> getAllProduct(){
      List<Product> allProduct =  productRepository.findAll();
      return allProduct.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product){
        return ProductResponse.builder()
                .productID(product.getProductID())
                .productName(product.getProductName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
