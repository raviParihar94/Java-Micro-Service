package dev.beelive.com.product_service.repository;

import dev.beelive.com.product_service.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product,String> {
}
