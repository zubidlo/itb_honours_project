package controllers;


import java.io.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import models.User;
import play.Play;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.*;
import views.html.dashboard.*;

public class Dashboard extends Controller {

	@Security.Authenticated(Secured.class)
    public Result index() {
		User user = User.findByEmail(request().username());
		if(user == null) redirect(routes.Application.index());
        return ok(index.render(user));
    }
    
	@Security.Authenticated(Secured.class)
    public Result lecture(String file) {
    	
    	User user = User.findByEmail(request().username());
    	if(user == null) redirect(routes.Application.index());
    	String path = "public/lectures/" + file;
    	try(InputStream in = Play.application().classloader().getResourceAsStream(path)) {
    		Stream<String> lines = new BufferedReader(new InputStreamReader(in)).lines();
            String html = lines.collect(Collectors.joining());
            return ok(lecture.render(user, new Html(html.toString())));
    	} catch (IOException e) { 
    		e.printStackTrace();
    		return notFound(index.render(user));
    	}
    }
}
