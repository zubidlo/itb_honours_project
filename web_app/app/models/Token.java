package models;

import models.utils.Mail;
import play.Configuration;
import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;

import com.avaje.ebean.Model;

import play.i18n.Messages;
import play.libs.mailer.MailerClient;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
public class Token extends Model {

    private static final int EXPIRATION_DAYS = 1;


    public enum TypeToken {
        password("reset"), email("email");
        private String urlPath;

        TypeToken(String urlPath) {
            this.urlPath = urlPath;
        }
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

    // -- Queries
    @SuppressWarnings("deprecation")
    public static Model.Finder<String, Token> find = new Finder<String, Token>(String.class, Token.class);

    public static Token findByTokenAndType(String token, TypeToken type) {
        return find.where().eq("token", token).eq("type", type).findUnique();
    }

    public boolean isExpired() {
        return dateCreation != null && dateCreation.before(expirationTime());
    }

    private Date expirationTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, -EXPIRATION_DAYS);
        return cal.getTime();
    }

    private Token getNewToken(User user, TypeToken type, String email) {
        Token token = new Token();
        token.token = UUID.randomUUID().toString();
        token.userId = user.id;
        token.type = type;
        token.email = email;
        token.save();
        return token;
    }

    public void sendMailResetPassword(User user, MailerClient mc) throws MalformedURLException {
        sendMail(user, TypeToken.password, null, mc);
    }

    public void sendMailChangeMail(User user, @Nullable String email,MailerClient mc) throws MalformedURLException {
        sendMail(user, TypeToken.email, email,mc );
    }

    private void sendMail(User user, TypeToken type, String email, MailerClient mc) throws MalformedURLException {

        Token token = getNewToken(user, type, email);
        String externalServer = Configuration.root().getString("server.hostname");

        String subject = null;
        String message = null;
        String toMail = null;

        String urlString = "http://" + externalServer + "/" + type.urlPath + "/" + token.token;
        URL url = new URL(urlString);

        switch (type) {
            case password:
                subject = Messages.get("mail.reset.ask.subject");
                message = Messages.get("mail.reset.ask.message", url.toString());
                toMail = user.email;
                break;
            case email:
                subject = Messages.get("mail.change.ask.subject");
                message = Messages.get("mail.change.ask.message", url.toString());
                toMail = token.email; // == email parameter
                break;
        }

        Logger.debug("sendMailResetLink: url = " + url);
        Mail.Envelop envelop = new Mail.Envelop(subject, message, toMail);
        Mail mailer = new Mail(mc);
        mailer.sendMail(envelop);
    }

}
