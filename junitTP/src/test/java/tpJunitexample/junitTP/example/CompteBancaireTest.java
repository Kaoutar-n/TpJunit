package tpJunitexample.junitTP.example;




import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class CompteBancaireTest {

    private CompteBancaire compte;
    private NotificationService notificationServiceMock;

    @BeforeEach
    public void setUp() {
        notificationServiceMock = Mockito.mock(NotificationService.class);
        compte = new CompteBancaire(100.0, notificationServiceMock);
    }

    @Test
    public void createInitialSoldeTest() {
        assertEquals(100.0, compte.getSolde(), 0.001);
    }

    @Test
    public void soldeDepTest() {
        compte.deposer(50.0);
        assertEquals(150.0, compte.getSolde(), 0.001);
        verify(notificationServiceMock).envoyerNotification("Dépôt de 50.0 effectué.");
    }


    @Test
    public void valideRetrTest() {
        compte.retirer(30.0);
        assertEquals(70.0, compte.getSolde(), 0.001);
        verify(notificationServiceMock).envoyerNotification("Retrait de 30.0 effectué.");
    }

    @Test
    public void retraitSupSoldeSansNotificationTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> compte.retirer(200.0));
        assertEquals("Fonds insuffisants.", exception.getMessage());
        verify(notificationServiceMock, never()).envoyerNotification(anyString());
    }
    @Test
    public void depotMontantNegatifTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> compte.deposer(-10.0));
        assertEquals("Le montant du dépôt doit être positif.", exception.getMessage());
        verify(notificationServiceMock, never()).envoyerNotification(anyString());
    }

    @Test
    public void retMontantNegatifTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> compte.retirer(-10.0));
        assertEquals("Le montant du retrait doit être positif.", exception.getMessage());
        verify(notificationServiceMock, never()).envoyerNotification(anyString());
    }

    @Test
    public void creationCompteSoldeNegatifTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new CompteBancaire(-50.0, notificationServiceMock));
        assertEquals("Le solde initial ne peut pas être négatif.", exception.getMessage());
    }
    @Test
    public void deuxDepotsNotificationTest() {
        compte.deposer(50.0);
        compte.deposer(30.0);
        assertEquals(180.0, compte.getSolde(), 0.001);
        verify(notificationServiceMock, times(2)).envoyerNotification(anyString());
    }
    @Test
    public void transfertEntreComptesTest() {
        NotificationService autreNotificationServiceMock = Mockito.mock(NotificationService.class);
        CompteBancaire autreCompte = new CompteBancaire(50.0, autreNotificationServiceMock);

        compte.transfererVers(autreCompte, 30.0);

        assertEquals(70.0, compte.getSolde(), 0.001);
        assertEquals(80.0, autreCompte.getSolde(), 0.001);

        verify(notificationServiceMock).envoyerNotification("Transfert de 30.0 effectué vers le compte destinataire.");
        verify(autreNotificationServiceMock).envoyerNotification("Réception de 30.0 du compte source.");
    }
}
