package controllers;

import static play.data.Form.form;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import controllers.Application.Login;
import controllers.Application.Register;
import models.User;
import models.utils.AppException;
import play.Logger;
import play.Play;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.*;
import views.html.index;
import views.html.dashboard.create;
import views.html.dashboard.lecture;

@Security.Authenticated(Secured.class)
public class Dashboard extends Controller {

    public Result index() {
        return ok(create.render(User.findByEmail(request().username())));
    }
    
    public Result lecture(String file) throws Exception {
    	String email = ctx().session().get("email");
    	if (email == null) {
    		session().clear();
    		return ok(index.render(form(Register.class), form(Login.class)));
    	}
    	
    	User user = User.findByEmail(email);
    	String path = "public/lectures/" + file;
    	File f = Play.application().getFile(path);
    	BufferedReader br = new BufferedReader(new FileReader(f));
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
