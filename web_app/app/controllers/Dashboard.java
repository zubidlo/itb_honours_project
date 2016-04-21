package controllers;

import static play.data.Form.form;

import java.io.*;
import java.nio.file.*;
import java.net.*;

import controllers.Application.Login;
import controllers.Application.Register;
import models.User;
import models.utils.AppException;
import play.Logger;
import play.Play;
import play.Environment;
import play.Application;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.*;
import views.html.index;
import views.html.dashboard.create;
import views.html.dashboard.lecture;

public class Dashboard extends Controller {

	@Security.Authenticated(Secured.class)
    public Result index() {
        return ok(create.render(User.findByEmail(request().username())));
    }
    
	@Security.Authenticated(Secured.class)
    public Result lecture(String file) throws Exception {
    	String email = ctx().session().get("email");
    	if (email == null) {
    		session().clear();
    		return ok(index.render(form(Register.class), form(Login.class)));
    	}
    	
    	User user = User.findByEmail(email);
    	String path = "public/lectures/" + file;
        InputStream in = Play.application().classloader().getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuffer fileContents = new StringBuffer();
        String line = br.readLine();
        while (line != null) {
            fileContents.append(line);
            line = br.readLine();
        }
        br.close();
    	return ok(lecture.render(user, new Html(fileContents.toString())));
    }
}
