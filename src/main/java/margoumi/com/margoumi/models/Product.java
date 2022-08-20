package margoumi.com.margoumi.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;


@Entity
@Table(name = "product")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;


    @Column(name = "normal_price")
    private BigDecimal nprix;

    @Column(name = "gros_price")
    private BigDecimal gprix;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "image")
    private String image;

    @Column(name = "create_date")
    @CreationTimestamp
    private Date createdDate;

    @Column(name = "updated_date")
    @UpdateTimestamp
    private Date updatedDate;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryProduct category ;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "image", fetch = FetchType.EAGER)
    private Set<FileInfo> products;


    @JsonManagedReference(value="prodcom")
    @OneToMany(cascade=CascadeType.PERSIST,mappedBy = "produit",fetch=FetchType.LAZY)
    private List<Commentaire> commentaire;

    private Double etoile;
    @ElementCollection
    private Map<Long, Double> clientEtoile;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "WISHLIST", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "client_id"))
    private Set<User> whoWhishesThisProduct;
}
