package models;

import models.utils.Mail;
import play.Configuration;

import play.data.format.Formats;
import play.data.validation.Constraints;

import com.avaje.ebean.Model;

import play.i18n.Messages;
import play.libs.mailer.MailerClient;

import javax.annotation.Nullable;
import javax.persistence.*;

import java.util.*;

@Entity
public class Token extends Model {

    private static final int EXPIRATION_DAYS = 1;

    public enum TypeToken {
        password("reset"), 
        email("email");
        private final String urlPath;
        private TypeToken(String path) { urlPath = path; }
    }

    @Id
    public String token;

    @Constraints.Required
    @Formats.NonEmpty
    public Long userId;

    @Constraints.Required
    @Enumerated(EnumType.STRING)
    public TypeToken type;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date dateCreation;

    @Constraints.Required
    @Formats.NonEmpty
    public String email;

    public static final Model.Finder<String, Token> FIND = new Finder<String, Token>(Token.class);

    //tested
    public static Token findByTokenAndType(String token, TypeToken type) {
        return FIND.where().eq("token", token).eq("type", type).findUnique();
    }
    
    public Token() {
    	dateCreation = Calendar.getInstance().getTime();
    }
    
    public Token(String t, Long uid, TypeToken tt, String e) {
    	this();
    	token = t;
    	userId = uid;
    	type = tt;
    	email = e;
    }

    //tested
    public boolean isExpired() {
        return dateCreation.before(expirationTime());
    }
 
    //tested
    private Date expirationTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, -EXPIRATION_DAYS);
        return cal.getTime();
    }

    public void sendMailResetPassword(User user, MailerClient mc) {
        sendMail(user, TypeToken.password, null, mc);
    }

    public void sendMailChangeMail(User user, @Nullable String email,MailerClient mc){
        sendMail(user, TypeToken.email, email, mc );
    }

    private void sendMail(User user, TypeToken type, String email, MailerClient mc) {

    	String token = UUID.randomUUID().toString();
        new Token(token, user.id, type, email).save();
        
        String urlString = "http://" + Configuration.root().getString("server.hostname") + "/" + type.urlPath + "/" + token;
        String subject = null;
        String message = null;
        String toMail = null;
        
        switch (type) {
            case password:
                subject = Messages.get("mail.reset.ask.subject");
                message = Messages.get("mail.reset.ask.message", urlString);
                toMail = user.email;
                break;
            case email:
                subject = Messages.get("mail.change.ask.subject");
                message = Messages.get("mail.change.ask.message", urlString);
                toMail = email;
        }

        Mail.Envelope envelope = new Mail.Envelope(subject, message, toMail);
        Mail mailer = new Mail(mc);
        mailer.sendMail(envelope);
    }

}
