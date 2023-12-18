package com.ideao.springboot.controllers;

import com.ideao.springboot.dtos.ProductRecordDto;
import com.ideao.springboot.models.ProductModel;
import com.ideao.springboot.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ProductController {
    @Autowired
    private ProductRepository productRepository;
    @GetMapping("/products")
    public ResponseEntity<Page<ProductModel>> getAllProducts(
            @PageableDefault(size=10, sort = {"name"}) Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal value
            ){
        Page<ProductModel> productsList = this.productRepository.findAllWithFilters(name, value, pageable);
        if(!productsList.isEmpty()){
            for(ProductModel product: productsList){
                UUID id = product.getId();
                product.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(productsList);
    }
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductModel> getOneProduct(@PathVariable(value = "id") UUID id){
        Optional<ProductModel> product0 = this.productRepository.findById(id);
        if(product0.isEmpty()){
            throw new EntityNotFoundException();
        }
        product0.get().add(linkTo(methodOn(ProductController.class)
                .getAllProducts(null, null, null)).withRel("Products List"));
        return ResponseEntity.status(HttpStatus.OK).body(product0.get());
    }
    @PostMapping("/products")
    @Transactional
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productRecordDto){
        var productModel = new ProductModel();
        BeanUtils.copyProperties(productRecordDto, productModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.productRepository.save(productModel));
    }

    @DeleteMapping("/products/{id}")
    @Transactional
    public ResponseEntity<Object> deleteProduct(@PathVariable(value="id") UUID id){
        Optional<ProductModel> productO = this.productRepository.findById(id);
        if(productO.isEmpty()){
            throw new EntityNotFoundException();
        }
        this.productRepository.delete(productO.get());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/products/{id}")
    @Transactional
    public ResponseEntity<ProductModel> updateProduct(@PathVariable(value="id") UUID id, @RequestBody @Valid ProductRecordDto productRecordDto){
        Optional<ProductModel> product0 = this.productRepository.findById(id);
        if(product0.isEmpty()){
            throw new EntityNotFoundException();
        }
        var productModel = product0.get();
        BeanUtils.copyProperties(productRecordDto, productModel);
        return ResponseEntity.status(HttpStatus.OK).body(this.productRepository.save(productModel));
    }
}