package org.shoppingdashboard.Controller;

import lombok.extern.log4j.Log4j2;
import org.shoppingdashboard.Entity.BrandingSettings;
import org.shoppingdashboard.Entity.Product;
import org.shoppingdashboard.Service.BrandingService;
import org.shoppingdashboard.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;

@Controller
@Log4j2
//@RequestMapping("/admin/product-management")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private BrandingService brandingService;

@GetMapping("admin/product")
public String showProductManagementPage(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "20") int size,
                                        @RequestParam(defaultValue = "") String name,
                                        Model model) {
    log.info("Invoking showProductManagementPage...");

    // Page numbers are 0-indexed in Spring Data JPA, so subtract 1 from the page number
    Page<Product> products = productService.getProducts(name, PageRequest.of(page - 1, size));

    log.info("Products fetched: " + products.getContent());
    log.info("Total pages: " + products.getTotalPages());

    // Add attributes to the model for rendering in the view
    model.addAttribute("products", products.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", products.getTotalPages());
    model.addAttribute("name", name); // Retain search query
    model.addAttribute("size", size); // Retain page size
    model.addAttribute("isEmpty", products.getContent().isEmpty());

    return "admin_product_manage"; // Return the view name
}
  @PostMapping(value = "/admin/product/create", consumes = "multipart/form-data")
@ResponseBody
public Product createProduct(@RequestParam("image") MultipartFile image, @RequestParam("name") String name,
                             @RequestParam("description") String description, @RequestParam("price") double price,
                             @RequestParam("stockQuantity") int stockQuantity) {
    try {
        // Define an external folder path
        String externalFolderPath = "C:/Users/dell/Desktop/shop/Shopping-dashboard/images/";

        File folder = new File(externalFolderPath);

        // Create the folder if it does not exist
        if (!folder.exists() && !folder.mkdirs()) {
            throw new RuntimeException("Failed to create folder: " + externalFolderPath);
        }

        // Save the image file
        String fileName = image.getOriginalFilename();
        File file = new File(externalFolderPath + fileName);
        image.transferTo(file);

        // Create the product object
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        product.setImageUrl("/images/" + fileName); // Set the relative image path

        // Save the product
        return productService.createProduct(product);
    } catch (Exception e) {
        throw new RuntimeException("Failed to create product", e);
    }
}

@PostMapping(value = "/admin/product/update/{id}", consumes = "multipart/form-data")
@ResponseBody
public Product updateProduct(@PathVariable Long id,
                             @RequestParam(value = "image", required = false) MultipartFile image,
                             @RequestParam("name") String name,
                             @RequestParam("description") String description,
                             @RequestParam("price") double price,
                             @RequestParam("stockQuantity") int stockQuantity) {
    try {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Update product fields
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);

        // Handle image upload if provided
        if (image != null && !image.isEmpty()) {
            String externalFolderPath = "C:/Users/dell/Desktop/shop/Shopping-dashboard/images/";
            File folder = new File(externalFolderPath);

            if (!folder.exists() && !folder.mkdirs()) {
                throw new RuntimeException("Failed to create folder: " + externalFolderPath);
            }

            String fileName = image.getOriginalFilename();
            File file = new File(externalFolderPath + fileName);
            image.transferTo(file);

            product.setImageUrl("/images/" + fileName); // Set the relative image path
        }

        return productService.updateProduct(id, product);
    } catch (Exception e) {
        throw new RuntimeException("Failed to update product", e);
    }
}
    @DeleteMapping("/admin/product/delete/{id}")
    @ResponseBody
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }



   @GetMapping("/customer/product")
public String showProductPage(@RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "9") int size,
                              @RequestParam(defaultValue = "") String name,
                              @RequestParam(defaultValue = "asc") String sort,
                              @RequestParam(required = false) Long id,
                              Model model) {
    // Create PageRequest with sorting logic
    PageRequest pageRequest = PageRequest.of(page - 1, size, sort.equals("asc") ? Sort.by("price").ascending() : Sort.by("price").descending());

    // Fetch products with sorting and pagination
    Page<Product> products = productService.getProducts(name, pageRequest);
    System.out.println("total pages: " + products.getTotalPages() + ", current page: " + page + ", size: " + size);

    // Add attributes to the model
    model.addAttribute("products", products.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", products.getTotalPages());
    model.addAttribute("name", name);
    model.addAttribute("sort", sort);
       BrandingSettings settings = brandingService.getSettings();
       model.addAttribute("settings", settings);

    if (id != null) {
        Product product = productService.getProductById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        model.addAttribute("product", product);
    }

    return "customer_product_page";
}

@GetMapping("/customer/product/details/{id}")
public String showProductDetails(@PathVariable Long id, Model model) {
    Product product = productService.getProductById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    model.addAttribute("product", product);
    BrandingSettings settings = brandingService.getSettings();
    model.addAttribute("settings", settings);
    return "customer_product_details";


}
@GetMapping("/customer/cart/add/{id}")
public String addToCart(@PathVariable Long id, @RequestParam(defaultValue = "1") int quantity, HttpSession session, Model model) {


    Long customerId;
    if (session.getAttribute("impersonationMode") != null) {
        customerId = (Long) session.getAttribute("impersonatedCustomerId");
        System.out.println("Impersonated Customer ID inn addtocart: " + customerId);
        model.addAttribute("message", "Item added to cart for impersonated customer.");
    } else {
        customerId = (Long) session.getAttribute("customerId");
        model.addAttribute("message", "Item added to cart.");
    }

    if (customerId != null) {
        BrandingSettings settings = brandingService.getSettings();
        model.addAttribute("settings", settings);
        productService.addProductToCart(id,customerId, quantity);
    } else {
        model.addAttribute("error", "Customer ID not found.");
    }
    return "redirect:/customer/product";
}

@GetMapping("/customer/cart")
public String showCartPage(Model model) {
    model.addAttribute("cartItems", productService.getCartItems());
    return "customer_cart_page"; // View for displaying cart items
}


@GetMapping("/admin/product/details")
public ResponseEntity<Product> getProductDetails(@RequestParam String name) {
    Product product = productService.findByName(name);
    if (product != null) {
        return ResponseEntity.ok(product);
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}

@PostMapping("/admin/product/upload-image")
@ResponseBody
public String uploadImage(@RequestParam("image") MultipartFile image, @RequestParam("productId") Long productId) {
    try {
        // Define an external folder path
        String externalFolderPath = "C:/Users/dell/Desktop/shop/Shopping-dashboard/images/";
        File folder = new File(externalFolderPath);

        // Create the folder if it does not exist
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                System.out.println("Folder created: " + externalFolderPath);
            } else {
                throw new RuntimeException("Failed to create folder: " + externalFolderPath);
            }
        }

        // Save the image file
        String fileName =  image.getOriginalFilename();
        File file = new File(externalFolderPath + fileName);
        image.transferTo(file);

        // Return the relative path to be saved in the database
        String imagePath = "/images/" + fileName;
        productService.updateProductImage(productId, imagePath);

        return imagePath;
    } catch (Exception e) {
        throw new RuntimeException("Failed to upload image", e);
    }
}
}


