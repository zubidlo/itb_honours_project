package models;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;

import java.util.UUID;

import models.utils.AppException;
import models.utils.Hash;

import org.junit.Before;
import org.junit.Test;

import play.test.WithApplication;

public class TokenTests extends WithApplication {

	private String token, token1;
	private User user;
	private String email;
		
	@Before public void setUp() throws AppException {
		start(fakeApplication(inMemoryDatabase()));
        String hash = Hash.createPassword("password1");
    	token = UUID.randomUUID().toString();
    	token1 = UUID.randomUUID().toString();
    	email = "martin@gmail.com";
    	user = new User(email, "Martin Zuber", hash, token);
    	user.save();
    	new Token(token, user.id, Token.TypeToken.password, email).save();
    	new Token(token1, user.id, Token.TypeToken.email, email).save();
	}
	
	@Test public void tryFindByTokenAndType() {
		Token t1 = Token.findByTokenAndType(token, Token.TypeToken.password);
		assertNotNull(t1);
		assertEquals(token, t1.token);
		Token t2 = Token.findByTokenAndType(token1, Token.TypeToken.email);
		assertNotNull(t2);
		assertEquals(token1, t2.token);
	}
	
	@Test public void tryIsExpired() {
		Token t1 = Token.findByTokenAndType(token, Token.TypeToken.password);
		assertNotNull(t1);
		assertNotNull(t1.dateCreation);
		Token t2 = Token.findByTokenAndType(token1, Token.TypeToken.email);
		assertNotNull(t2);
		assertNotNull(t2.dateCreation);
	}
	
	@Test public void tryExpirationTime() {
		Token t1 = Token.findByTokenAndType(token, Token.TypeToken.password);
		assertNotNull(t1);
		assertNotNull(t1.dateCreation);
		assertFalse(t1.isExpired());
		
		Token t2 = Token.findByTokenAndType(token1, Token.TypeToken.email);
		assertNotNull(t2);
		assertNotNull(t2.dateCreation);
		assertFalse(t2.isExpired());
	}
}
