package comptoirs.service;

import comptoirs.entity.Ligne;
import comptoirs.entity.Produit;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
 // Ce test est basé sur le jeu de données dans "test_data.sql"
class LigneServiceTest {
    static final int NUMERO_COMMANDE_DEJA_LIVREE = 99999;
    static final int NUMERO_COMMANDE_PAS_LIVREE  = 99998;
    static final int REFERENCE_PRODUIT_DISPONIBLE_1 = 93;
    static final int REFERENCE_PRODUIT_DISPONIBLE_2 = 94;
    static final int REFERENCE_PRODUIT_DISPONIBLE_3 = 95;
    static final int REFERENCE_PRODUIT_DISPONIBLE_4 = 96;
    static final int REFERENCE_PRODUIT_INDISPONIBLE = 97;
    static final int UNITES_COMMANDEES_AVANT = 0;

    @Autowired
    LigneService service;

    @Test
    void onPeutAjouterDesLignesSiPasLivre() {
        var ligne = service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, REFERENCE_PRODUIT_DISPONIBLE_1, 1);
        assertNotNull(ligne.getId(),
        "La ligne doit être enregistrée, sa clé générée"); 
    }

    @Test
    void laQuantiteEstPositive() {
        assertThrows(ConstraintViolationException.class, 
            () -> service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, REFERENCE_PRODUIT_DISPONIBLE_1, 0),
            "La quantite d'une ligne doit être positive");
    }

    @Test
    void pasAjoutLigneProduitInexistant(){
        // Le produit doit exister
        assertThrows(NoSuchElementException.class, () ->
                service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE,1029,1));
    }
    @Test
    void pasAjoutLigneCommandeInexistante(){
        // La commande doit exister
        assertThrows(NoSuchElementException.class, () ->
                service.ajouterLigne(-1,98,1));
    }
    @Test
    void pasAjoutLigneDejaLivree(){
        // La commande ne doit pas être déjà envoyée
        assertThrows(UnsupportedOperationException.class, () ->
                service.ajouterLigne(NUMERO_COMMANDE_DEJA_LIVREE,98,1));
    }

    @Test
    void stockSuffisant(){
        // On doit avoir une quantité en stock du produit suffisante
        var ligne = service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, REFERENCE_PRODUIT_DISPONIBLE_1, 1);
        assertTrue(ligne.getQuantite() > ligne.getProduit().getUnitesEnStock());
    }

}
