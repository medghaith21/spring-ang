package margoumi.com.margoumi.service;

import margoumi.com.margoumi.models.Product;
import margoumi.com.margoumi.models.User;
import margoumi.com.margoumi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import margoumi.com.margoumi.repository.ProductRepository;

import java.util.List;
import java.util.Map;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }
    public List<Product> findAllProduct( ){
        return productRepository.findAll();
    }

    public Product findProdById(Long id) throws Exception{
        return productRepository.findProductById(id)
                .orElseThrow(() -> new Exception("User by id " + id + " was not found"));
    }

    public long addProduct(Product product){
        return productRepository.save(product).getId();
    }

    public long editProduct(Product product){
        return  productRepository.save(product).getId();
    }

    public void deleteProduct(Long id){
        productRepository.deleteById(id);
    }

    public Product updateProduit(Product u) {
        // TODO Auto-generated method stub
        return productRepository.save(u);
    }


    public void calculeEtoile(Double rev,Long idP,Long idC) throws Exception{

        Product p=findProdById(idP);
        Double rectif=null;
        User c=userRepository.findById(idC).orElse(null);
        Map<Long,Double> temp = p.getClientEtoile();
        Double sum=0.0;
        if(((p!=null) )&&(rev>=0 && rev<=5)){
            temp.put(idC, rev);
            for (Double value : temp.values()) {
                sum += value;

            }
            rectif=sum/temp.size();
            if(rectif>=0 && rectif<0.5){
                p.setEtoile(0.0);
                p.setClientEtoile(temp);
            }else if (rectif>=0.5 && rectif<1){
                p.setEtoile(0.5);
                p.setClientEtoile(temp);
            }else if (rectif>=1 && rectif<1.5){
                p.setEtoile(1.0);
                p.setClientEtoile(temp);
            }else if (rectif>=1.5 && rectif<2){
                p.setEtoile(1.5);
                p.setClientEtoile(temp);
            }else if (rectif>=2 && rectif<2.5){
                p.setEtoile(2.0);
                p.setClientEtoile(temp);
            }else if (rectif>=2.5 && rectif<3){
                p.setEtoile(2.5);
                p.setClientEtoile(temp);
            }else if (rectif>=3 && rectif<3.5){
                p.setEtoile(3.0);
                p.setClientEtoile(temp);
            }else if (rectif>=3.5 && rectif<4){
                p.setEtoile(3.5);
                p.setClientEtoile(temp);
            }else if (rectif>=4 && rectif<4.5){
                p.setEtoile(4.0);
                p.setClientEtoile(temp);
            }else if (rectif>=4.5 && rectif<4.75){
                p.setEtoile(4.5);
                p.setClientEtoile(temp);
            }else if (rectif>=4.75 && rectif<=5){
                p.setEtoile(5.0);
                p.setClientEtoile(temp);
            }
            updateProduit(p);
        }
    }
}
