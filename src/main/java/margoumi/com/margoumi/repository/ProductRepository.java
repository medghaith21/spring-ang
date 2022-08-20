package margoumi.com.margoumi.repository;

import margoumi.com.margoumi.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    Optional<Product> findProductById(Long id);
    Page<Product> findByCategoryId(@RequestParam("id") Long id, Pageable pageable);

    @Modifying
    @Query(value="delete from wishlist where product_id = :id",nativeQuery = true)
    void removeFromWishListWhenIsSold(@Param("id") long id);

    @Modifying
    @Query(value="delete from wishlist where product_id = :productId and client_id = :clientId",nativeQuery = true)
    void removeFromClientWishlist(@Param("productId") long productId, @Param("clientId") long clientId);


}
