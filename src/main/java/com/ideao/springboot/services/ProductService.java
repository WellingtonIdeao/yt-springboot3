package com.ideao.springboot.services;

import com.ideao.springboot.controllers.ProductController;
import com.ideao.springboot.dtos.ProductRecordDto;
import com.ideao.springboot.models.ProductModel;
import com.ideao.springboot.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Page<ProductModel> getAllProducts(Pageable pageable, String name, BigDecimal value){
        Page<ProductModel> productsList = this.productRepository.findAllWithFilters(name, value, pageable);
        if(!productsList.isEmpty()){
            for(ProductModel product: productsList){
                addLinkToSelfProduct(product);
            }
        }
        return productsList;
    }

    public ProductModel getOneProduct(UUID id){
        Optional<ProductModel> product0 = this.productRepository.findById(id);
        if(product0.isEmpty()){
            throw new EntityNotFoundException();
        }
        return addLinkToListProducts(product0.get());
    }

    public ProductModel saveProduct(ProductRecordDto productRecordDto){
        var productModel = new ProductModel();
        BeanUtils.copyProperties(productRecordDto, productModel);

        this.productRepository.save(productModel);
        return addLinkToSelfProduct(productModel);
    }

    public ProductModel updateProduct(UUID id, ProductRecordDto productRecordDto){
        Optional<ProductModel> product0 = this.productRepository.findById(id);
        if(product0.isEmpty()){
            throw new EntityNotFoundException();
        }
        var productModel = product0.get();
        BeanUtils.copyProperties(productRecordDto, productModel);
        this.productRepository.save(productModel);
        return addLinkToSelfProduct(productModel);

    }

    public void deleteProduct(UUID id){
        Optional<ProductModel> product0 = this.productRepository.findById(id);
        if(product0.isEmpty()){
            throw  new EntityNotFoundException();
        }
        this.productRepository.delete(product0.get());
    }

    private ProductModel addLinkToSelfProduct(ProductModel productModel){
        return productModel.add(
                linkTo(methodOn(ProductController.class)
                        .getOneProduct(productModel.getId()))
                        .withSelfRel());
    }
    private ProductModel addLinkToListProducts(ProductModel productModel){
        return productModel.add(
                linkTo(methodOn(ProductController.class)
                        .getAllProducts(null, null, null))
                        .withRel("ProductsList"));
    }
}
