package views;

import models.User;
import models.utils.AppException;
import models.utils.Hash;

import org.junit.Before;
import org.junit.Test;

import play.test.WithApplication;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

import java.util.ArrayList;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;

import org.junit.Test;

import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import play.twirl.api.Content;

public class ViewTests extends WithApplication{
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
	public void renderTemplate() {
	  Content html = views.html.layout.render(user, null);
	  assertEquals("text/html", html.contentType());
	}

}
