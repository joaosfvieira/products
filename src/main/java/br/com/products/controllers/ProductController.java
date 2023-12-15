package br.com.products.controllers;

import br.com.products.dtos.ProductRecordDto;
import br.com.products.models.ProductModel;
import br.com.products.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @PostMapping("/products")
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productRecordDto) {
        ProductModel productModel = new ProductModel();
        BeanUtils.copyProperties(productRecordDto, productModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductModel>> getAllProducts() {
        List<ProductModel> productModelList = productRepository.findAll();

        if(!productModelList.isEmpty()) {
            for(ProductModel productModel : productModelList) {
                UUID id = productModel.getIdProduct();
                productModel.add(linkTo(methodOn(ProductController.class).getProductById(id)).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(productModelList);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable(value = "id") UUID id) {
        Optional<ProductModel> product = productRepository.findById(id);

        if(product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }
        else {
            product.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withSelfRel());
            return ResponseEntity.status(HttpStatus.OK).body(product.get());
        }
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id, @RequestBody @Valid ProductRecordDto productRecordDto) {
        Optional<ProductModel> product = productRepository.findById(id);

        if(product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }
        else {
            ProductModel productModel = product.get();
            BeanUtils.copyProperties(productRecordDto, productModel);
            return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id) {
        Optional<ProductModel> product = productRepository.findById(id);

        if(product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }
        else {
            productRepository.delete(product.get());
            return ResponseEntity.status(HttpStatus.OK).body("Product was deleted.");
        }
    }
}
