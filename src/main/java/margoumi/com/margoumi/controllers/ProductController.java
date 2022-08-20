package margoumi.com.margoumi.controllers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import margoumi.com.margoumi.models.CategoryProduct;
import margoumi.com.margoumi.models.FileInfo;
import margoumi.com.margoumi.models.Product;
import margoumi.com.margoumi.repository.FileInfoRepository;
import margoumi.com.margoumi.repository.ProductRepository;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import margoumi.com.margoumi.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonMappingException;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
// @CrossOrigin("http://localhost:4200")
public class ProductController implements ServletContextAware {
    private final ProductService productService;


    ServletContext context;
    private final ProductRepository productRepository;




    @Autowired
    FileInfoRepository imageRepository;

    public ProductController(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts () {
        List<Product> products = productService.findAllProduct();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    private String saveImage(MultipartFile multipartFile) {
        try {
            byte[] bytes = multipartFile.getBytes();
            Path path = Paths.get(context.getRealPath("/Imagess/" + multipartFile.getOriginalFilename()));
            Files.write(path, bytes);
            return multipartFile.getOriginalFilename();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }

    @PostMapping("/addproduct")
    public long  newProduct(@RequestParam("files") MultipartFile[] files, @RequestParam("file") MultipartFile image,
                            @RequestParam("product") String product) throws JsonParseException, JsonMappingException , Exception {
        Product arti = new ObjectMapper().readValue(product, Product.class);
        boolean isExit = new File(context.getRealPath("/Imagess/")).exists();
        if (!isExit)
        {
            new File (context.getRealPath("/Imagess/")).mkdir();
            System.out.println("mk dir Imagess.............");
        }
        System.out.println("Save Article  22222.............");
        Set<FileInfo> photos = new HashSet<>();
        for (MultipartFile file : files) {
            FileInfo fileinfo = new FileInfo();
            String filename = file.getOriginalFilename();
            String newFileName = FilenameUtils.getBaseName(filename)+"."+FilenameUtils.getExtension(filename);
            File serverFile = new File (context.getRealPath("/Imagess/"+File.separator+newFileName));
            try
            {
                System.out.println("Image");
                FileUtils.writeByteArrayToFile(serverFile,file.getBytes());

            }catch(Exception e) {
                e.printStackTrace();
            }
            fileinfo.setName(newFileName);
            fileinfo.setImage(arti);
            imageRepository.save(fileinfo);
            photos.add(fileinfo);
        }

        String fileName = image.getOriginalFilename();
        String newFileName = FilenameUtils.getBaseName(fileName)+"."+FilenameUtils.getExtension(fileName);
        File serverFile = new File (context.getRealPath("/Imagess/"+File.separator+newFileName));
        try
        {
            System.out.println("Image");
            FileUtils.writeByteArrayToFile(serverFile,image.getBytes());

        }catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("Save Article 333333.............");
        arti.setImage(newFileName);

        System.out.println("Save Article 333333.............");
        // arti.setProducts(photos);
        return productService.addProduct(arti);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PutMapping("/update/{id}")
    public long updateEmployee(@PathVariable("id") Long id, @RequestParam("file") MultipartFile [] files,
                                                  @RequestParam("product") String product) throws JsonParseException, JsonMappingException , Exception  {
        Product arti = new ObjectMapper().readValue(product, Product.class);
        Product p = productService.findProdById(id);
        boolean isExit = new File(context.getRealPath("/Imagess/")).exists();
        if (!isExit)
        {
            new File (context.getRealPath("/Imagess/")).mkdir();
            System.out.println("mk dir Imagess.............");
        }
        System.out.println("Save Article  22222.............");
        Set<FileInfo> photos = new HashSet<>();
        for (MultipartFile file : files) {
            FileInfo fileinfo = new FileInfo();
            String filename = file.getOriginalFilename();
            String newFileName = FilenameUtils.getBaseName(filename)+"."+FilenameUtils.getExtension(filename);
            File serverFile = new File (context.getRealPath("/Imagess/"+File.separator+newFileName));
            try
            {
                System.out.println("Image");
                FileUtils.writeByteArrayToFile(serverFile,file.getBytes());

            }catch(Exception e) {
                e.printStackTrace();
            }
            fileinfo.setName(newFileName);
            fileinfo.setImage(arti);
            imageRepository.save(fileinfo);
            photos.add(fileinfo);
        }

        System.out.println("Save Article 333333.............");
        // arti.setProducts(photos);
        if(p.getId() > -1) {
            p.setName(arti.getName());
            p.setDescription(arti.getDescription());
            p.setNprix(arti.getNprix());
            p.setGprix(arti.getGprix());
            p.setCategory(arti.getCategory());
            p.setQuantity(arti.getQuantity());
            productService.deleteProduct(arti.getId());
            return p.getId();
        } else {
            return arti.getId();
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Product> getEmployeeById (@PathVariable("id") Long id) throws Exception {
        Product product = productService.findProdById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @GetMapping(path="/Img/{id}")
    public List<byte[]> getPhoto(@PathVariable("id") Long id) throws Exception{
        ArrayList<FileInfo> files = new ArrayList<FileInfo>();
        Product product = productService.findProdById(id);
        List<byte[]> fi = new ArrayList<>();
        files   =imageRepository.findByImage(product);
        System.out.println(files);

        for (FileInfo file : files) {
           // fi.add(Files.readAllBytes(Paths.get(context.getRealPath("/Imagess/")+file.getImage())));
            fi.add(Files.readAllBytes(Paths.get(context.getRealPath("/Imagess/")+file.getName())));
        }

    return fi;
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<List<FileInfo>> getImagesByProduct (@PathVariable("id") Long id) throws Exception {
        ArrayList<FileInfo> files = new ArrayList<FileInfo>();
        Product product = productService.findProdById(id);
        files   =imageRepository.findByImage(product);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @GetMapping(path="/getimage/{id}")
    public byte[] getPhotoProduct(@PathVariable("id") Long id) throws Exception{
        FileInfo Article   =imageRepository.findById(id).orElseThrow(() -> new Exception("File by id " + id + " was not found"));;
        return Files.readAllBytes(Paths.get(context.getRealPath("/Imagess/")+Article.getName()));
    }

    @GetMapping(path="/Imgarticles/{id}")
    public byte[] getProductImage(@PathVariable("id") Long id) throws Exception{
        Product Article   =productService.findProdById(id);
        return Files.readAllBytes(Paths.get(context.getRealPath("/Imagess/")+Article.getImage()));
    }

    @GetMapping("/search/findByCategoryId?id={id}")
    public ResponseEntity<Page<Product>> getAllProducts (@PathVariable("id") Long id, Pageable pageable) {
        Page<Product> products = productRepository.findByCategoryId(id,pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping ("/add-etoile/{produit-id}/{client-id}/{rev}")
    @ResponseBody
    public void moyEtoile(@PathVariable("produit-id") Long produitId,@PathVariable("client-id") Long clientId,@PathVariable("rev") Double rev ) throws Exception{
        productService.calculeEtoile(rev, produitId, clientId);
    }

}
