package margoumi.com.margoumi.service;

import margoumi.com.margoumi.models.Product;
import margoumi.com.margoumi.models.User;
import margoumi.com.margoumi.repository.ProductRepository;
import margoumi.com.margoumi.repository.UserRepository;
import margoumi.com.margoumi.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class WishlistService {

    @Autowired
    private UserService clientService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository clientRepo;

    @Autowired
    private ProductRepository productRepo;

    private User findClientById(long id)  {
        return clientService.findById(id);
    }

    private Product findProductById(long id) throws Exception {
        return productService.findProdById(id);
    }

    public Set<Product> findAll()  {
        UserPrincipal user = UserService.clientAuthenticated();
        User cli = findClientById(user.getId());
        //System.out.println(user.getId());
        return cli.getProductsWished();

    }

    public void markProductAsWished(long productId) throws Exception {
        UserPrincipal user = UserService.clientAuthenticated();
        System.out.println("post id"+user.getId());
        User client = findClientById(user.getId());
        Product product = findProductById(productId);


        System.out.println(user.getId());

        if (client.getProductsWished().contains(product)) {
            throw new Exception();
        }

        client.getProductsWished().add(product);
        product.getWhoWhishesThisProduct().add(client);

        clientRepo.save(client);
        productRepo.save(product);
    }

    @Transactional
    public void delete(long productId) {
        UserPrincipal user = UserService.clientAuthenticated();

        productRepo.removeFromClientWishlist(productId, user.getId());
    }

    @Transactional
    public void removeProductFromWishlistWhenIsSold(long productId) {
        productRepo.removeFromWishListWhenIsSold(productId);
    }
}