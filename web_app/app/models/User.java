package models;

import models.utils.AppException;
import models.utils.Hash;
import play.data.format.Formats;
import play.data.validation.Constraints;

import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import java.util.Calendar;
import java.util.Date;

@Entity
public class User extends Model {

    @Id
    public Long id;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(unique = true)
    public String email;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(unique = true)
    public String fullname;

    public String confirmationToken;

    @Constraints.Required
    @Formats.NonEmpty
    public String passwordHash;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date dateCreation;

    @Formats.NonEmpty
    public Boolean validated = false;

    public static final Model.Finder<Long, User> FIND = new Model.Finder<Long, User>(User.class);
    
    public User(String e, String n, String h, String t){
    	email = e;
    	fullname = n;
    	passwordHash = h;
    	confirmationToken = t;
    	dateCreation = Calendar.getInstance().getTime();
    }

    //tested
    public static User findByEmail(String email) {
        return email == null ? null : FIND.where().eq("email", email).findUnique();
    }

    //tested
    public static User findByConfirmationToken(String token) {
        return token == null ? null : FIND.where().eq("confirmationToken", token).findUnique();
    }

    //tested
    public static User authenticate(String email, String passw) {
    	if (email == null || passw == null) return null;
        User user = findByEmail(email);
        if (user != null && Hash.checkPassword(passw, user.passwordHash)) return user;
        return null;
    }

    //tested
    public void changePassword(String password) throws AppException{
        passwordHash = Hash.createPassword(password);
        save();
    }
}
