package margoumi.com.margoumi.controllers;

import margoumi.com.margoumi.models.Product;
import margoumi.com.margoumi.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Set;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/wish")
public class WishlistResource {

    @Autowired
    private WishlistService service;


    @GetMapping("/wishlist")
    public ResponseEntity<Set<Product>> returnWishlist() throws Exception {


        return ResponseEntity.ok().body(service.findAll());
    }
    @CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600)
    @PostMapping("/wishlist/{productId}")
    public ResponseEntity<Void> maskProductAsWished(@PathVariable long productId) throws Exception {

        service.markProductAsWished(productId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/wishlist/{productId}")
    public ResponseEntity<Void> delete(@PathVariable long productId){
        service.delete(productId);

        return ResponseEntity.noContent().build();
    }

}
