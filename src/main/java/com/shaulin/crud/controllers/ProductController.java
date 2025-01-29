package com.shaulin.crud.controllers;

import com.shaulin.crud.dtos.ProductDto;
import com.shaulin.crud.model.Product;
import com.shaulin.crud.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository repository;

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        List<Product> listProducts = repository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(listProducts);
    }

    @GetMapping("/pageable")
    public ResponseEntity<List<Product>> getAllPageable(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = repository.findAll(pageable);

        return ResponseEntity.ok(productPage.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Product>> getById(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(repository.findById(id));
    }

    @PostMapping
    public ResponseEntity save(@RequestBody ProductDto body) {
        var product = new Product();
        BeanUtils.copyProperties(body, product);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable(value = "id") Integer id) {
        Optional<Product> product = repository.findById(id);

        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        repository.delete(product.get());

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable(value = "id") Integer id, @RequestBody ProductDto body) {
        Optional<Product> product = repository.findById(id);

        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        var productToUpdate = product.get();
        BeanUtils.copyProperties(body, productToUpdate);

        return ResponseEntity.status(HttpStatus.OK).body(repository.save(productToUpdate));
    }
}
