package models;

import models.utils.AppException;
import models.utils.Hash;
import play.data.format.Formats;
import play.data.validation.Constraints;

import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

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

    public static User findByEmail(String email) {
        return FIND.where().eq("email", email).findUnique();
    }

    public static User findByConfirmationToken(String token) {
        return FIND.where().eq("confirmationToken", token).findUnique();
    }

    public static User authenticate(String email, String clearPassword) {
        User user = FIND.where().eq("email", email).findUnique();
        if (user != null && Hash.checkPassword(clearPassword, user.passwordHash)) return user;
        return null;
    }

    public void changePassword(String password) throws AppException {
        passwordHash = Hash.createPassword(password);
        save();
    }

    public static boolean confirm(User user) throws AppException {
        if (user == null) return false;
        user.confirmationToken = null;
        user.validated = true;
        user.save();
        return true;
    }

}
