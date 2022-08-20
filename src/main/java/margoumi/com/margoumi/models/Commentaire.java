package margoumi.com.margoumi.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment")
@Entity
public class Commentaire  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCommentaire ;

    private Long idClient;

    private String comment;

    private Long likes;
    @JsonBackReference(value="prodcom")
    @ManyToOne
    private Product produit;

    @CreationTimestamp
    private Date dateCreated;

    @Column(name = "last_updated")
    @UpdateTimestamp
    private Date lastUpdated;




}