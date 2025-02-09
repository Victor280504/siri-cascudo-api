package com.progweb.siri_cascudo_api.service;

import com.progweb.siri_cascudo_api.dto.ProductDTO;
import com.progweb.siri_cascudo_api.model.Product;
import com.progweb.siri_cascudo_api.repository.ProductRepository;
import com.progweb.siri_cascudo_api.util.LocalStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final LocalStorageService localStorageService;
    // private final FirebaseStorageService firebaseStorageService; // Firebase
    // desativado por enquanto

    public ProductService(ProductRepository productRepository, LocalStorageService localStorageService) {
        this.productRepository = productRepository;
        this.localStorageService = localStorageService;
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(ProductDTO::new)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public ProductDTO createProduct(String name, String description, Double price, int quantity, Long idCategory,
            MultipartFile image) throws IOException {
        String imageUrl = localStorageService.saveImage(image);
        // String imageUrl = firebaseStorageService.uploadImage(image.getBytes(),
        // image.getOriginalFilename()); // Firebase desativado

        Product product = new Product(null, name, description, imageUrl, quantity, price, idCategory);
        return new ProductDTO(productRepository.save(product));
    }

    public ProductDTO updateProduct(Long id, String name, String description, Double price, int quantity,
            Long idCategory, MultipartFile image) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setIdCategory(idCategory);

       
        if (image != null && !image.isEmpty()) {
            if (product.getImage() != null && !product.getImage().isEmpty()) {
                localStorageService.deleteImage(product.getImage()); 
            }
            String imageUrl = localStorageService.saveImage(image); 
            product.setImage(imageUrl);
        }

        return new ProductDTO(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}