package margoumi.com.margoumi.controllers;

import margoumi.com.margoumi.models.Commentaire;
import margoumi.com.margoumi.models.Product;
import margoumi.com.margoumi.models.User;
import margoumi.com.margoumi.service.CommentaireService;
import margoumi.com.margoumi.service.ProductService;
import margoumi.com.margoumi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/comment")
public class CommentaireController {
    @Autowired
    CommentaireService commentaireService;
    @Autowired
    ProductService produitService;

    @Autowired
    private UserService userService;

// http://localhost:8081/retrieve-all-commentaires

    @GetMapping("/retrieve-all-commentaires")
    @ResponseBody
    public List<Commentaire> getCommentaire() {
        List<Commentaire> listCommentaires = commentaireService.retrieveAllCommentaires();
        return listCommentaires;
    }

    @GetMapping(path="/getUser/{id}")
    public User getUSerCommentaire(@PathVariable("id") Long id) {
        User u = userService.findById(commentaireService.retrieveCommentaire(id).getIdClient());
        return u;
    }

    // http://localhost:8081/add-commentaire
    @PostMapping("/add-commentaire")
    @ResponseBody
    public Commentaire addCommentaire(@RequestBody Commentaire c) throws Exception{
        List<String> badWords= Collections.unmodifiableList(Arrays.asList("bob","fuck","shit","dick","sh*t","ass","bitch","bastard","cunt","trash","wanker","piss","pussy","twat","crap","arsehole","gash","prick","cock","minge","nigga","slut","damn","sucker","cracker","poop","puup","boob","buub","f*ck","b*tch"));
        Product p=produitService.findProdById(c.getProduit().getId());

        if(p==null){throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "NULL");}


        if(c.getComment().replaceAll("\\s+","").equals("")){
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE, "Empty");
        }

        for(String bW : badWords){
            if(c.getComment().toLowerCase().replaceAll("\\s+","").replaceAll("1", "i").replaceAll("!", "i").replaceAll("3", "e").replaceAll("4", "a").replaceAll("@", "a").replaceAll("5", "s").replaceAll("7", "t").replaceAll("0", "o").replaceAll("9", "g").contains(bW)){
                throw new ResponseStatusException( HttpStatus.NOT_ACCEPTABLE, "Bad Boy");}
        }

        for(Commentaire com :p.getCommentaire()){
            if(c.getComment().equals(com.getComment())){
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Duplicated");
            }
        }



        return commentaireService.addCommentaire(c);
    }

    // http://localhost:8081/retrieve-commentaire/2
    @GetMapping("/retrieve-commentaire/{commentaire-id}")
    @ResponseBody
    public Commentaire retrieveCommentaire(@PathVariable("commentaire-id") Long commentaireId) {
        return commentaireService.retrieveCommentaire(commentaireId);
    }


    // http://localhost:8081/remove-commentaire/{commentaire-id}
    @DeleteMapping("/remove-client/{commentaire-id}")
    @ResponseBody
    public void removeCommentaire(@PathVariable("commentaire-id") Long commentaireId) {
        commentaireService.deleteCommentaire(commentaireId);
    }

    // http://localhost:8081/modify-commentaire
    @PutMapping("/modify-commentaire")
    @ResponseBody
    public Commentaire modifyClient(@RequestBody Commentaire c) throws Exception {
        List<String> badWords=Collections.unmodifiableList(Arrays.asList("bob","fuck","shit","dick","sh*t","ass","bitch","bastard","cunt","trash","wanker","piss","pussy","twat","crap","arsehole","gash","prick","cock","minge","nigga","slut","damn","sucker","cracker","poop","puup","boob","buub","f*ck","b*tch"));
        Product p=produitService.findProdById(c.getProduit().getId());

        if(p==null){throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "NULL");}


        if(c.getComment().replaceAll("\\s+","").equals("")){
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE, "Empty");
        }

        for(String bW : badWords){
            if(c.getComment().toLowerCase().replaceAll("\\s+","").replaceAll("1", "i").replaceAll("!", "i").replaceAll("3", "e").replaceAll("4", "a").replaceAll("@", "a").replaceAll("5", "s").replaceAll("7", "t").replaceAll("0", "o").replaceAll("9", "g").contains(bW)){throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE, "Bad Boy");}
        }

        for(Commentaire com :p.getCommentaire()){
            if(c.getComment().equals(com.getComment()) && c.getLikes()==com.getLikes()){
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Duplicated");
            }
        }
        return commentaireService.updateCommentaire(c);
    }
}
