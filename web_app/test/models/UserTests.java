package models;

import java.util.UUID;

import models.*;
import models.utils.AppException;
import models.utils.Hash;

import org.junit.*;

import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class UserTests extends WithApplication {
	
	private String token;
	private User user;
	
    @Before
    public void setUp() throws AppException {
        start(fakeApplication(inMemoryDatabase()));
        String hash = Hash.createPassword("password1");
    	token = UUID.randomUUID().toString();
    	user = new User("martin@gmail.com", "Martin Zuber", hash, token);
    	user.save();
    }
    
    @Test
    public void createAndRetrieveUser() {
    	User martin = User.findByEmail("martin@gmail.com");
        assertNotNull(martin);
        assertEquals("Martin Zuber", martin.fullname);
        assertNotNull(martin.dateCreation);
        
        martin = User.findByConfirmationToken(token);
        assertNotNull(martin);
        assertEquals("Martin Zuber", martin.fullname);
        assertNotNull(martin.dateCreation);
    }
    
    @Test
    public void tryAuthenticateUser() {
       	assertNotNull(User.authenticate("martin@gmail.com", "password1"));
        assertNull(User.authenticate("bob@gmail.com", "badpassword"));
    }
    
    @Test
    public void tryChangePassword() throws AppException {
    	user.changePassword("password2");
    	User martin = User.findByEmail("martin@gmail.com");
    	assertNotNull(martin);
    	assertNotNull(martin.dateCreation);
    	assertNotNull(User.authenticate("martin@gmail.com", "password2"));
    }
}
