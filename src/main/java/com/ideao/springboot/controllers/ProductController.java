package com.ideao.springboot.controllers;

import com.ideao.springboot.dtos.ProductRecordDto;
import com.ideao.springboot.models.ProductModel;
import com.ideao.springboot.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<Page<ProductModel>> getAllProducts(
            @PageableDefault(size=10, sort = {"name"}) Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal value
            ){
        var productsList = this.productService.getAllProducts(pageable, name, value);
        return ResponseEntity.status(HttpStatus.OK).body(productsList);
    }
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductModel> getOneProduct(@PathVariable(value = "id") UUID id){
        var product0 = this.productService.getOneProduct(id);
        return ResponseEntity.status(HttpStatus.OK).body(product0);
    }
    @PostMapping("/products")
    @Transactional
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productRecordDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.productService.saveProduct(productRecordDto));
    }

    @DeleteMapping("/products/{id}")
    @Transactional
    public ResponseEntity<Object> deleteProduct(@PathVariable(value="id") UUID id){
        this.productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/products/{id}")
    @Transactional
    public ResponseEntity<ProductModel> updateProduct(@PathVariable(value="id") UUID id, @RequestBody @Valid ProductRecordDto productRecordDto){
        return ResponseEntity.status(HttpStatus.OK).body(this.productService.updateProduct(id, productRecordDto));
    }
}