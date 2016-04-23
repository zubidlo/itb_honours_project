package controllers.account;

import controllers.Application;
import models.User;
import models.utils.AppException;
import models.utils.Hash;
import models.utils.Mail;
import org.apache.commons.mail.EmailException;
import play.Configuration;
import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.libs.mailer.MailerClient;

import javax.inject.Inject;
import views.html.account.register.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static play.data.Form.form;

public class Register extends Controller {
    @Inject
    MailerClient mailerClient;

    public Result index() {
        return ok(index.render(form(Application.Register.class)));
    }

    public Result save() throws AppException {
        Form<Application.Register> registerForm = form(Application.Register.class).bindFromRequest();

        if (registerForm.hasErrors()) {
            return badRequest(index.render(registerForm));
        }

        Application.Register register = registerForm.get();
                
        if (User.findByEmail(register.email) != null) {
            flash("error", Messages.get("error.email.already.exist"));
            return badRequest(index.render(registerForm));
        }

        User user = new User(register.email, register.fullname, Hash.createPassword(register.inputPassword), UUID.randomUUID().toString());
        user.save();
        String subject = Messages.get("mail.confirm.subject");
        String urlString = "http://" + Configuration.root().getString("server.hostname") + "/confirm/" + user.confirmationToken;
        String message = Messages.get("mail.confirm.message", urlString);
        Mail.Envelope envelope = new Mail.Envelope(subject, message, user.email);
        Mail mailer = new Mail(mailerClient);
        mailer.sendMail(envelope);
        return ok(registered.render());
    }

    public Result confirm(String token) {
        User user = User.findByConfirmationToken(token);
        
        if (user == null) {
            flash("error", Messages.get("error.unknown.email"));
            return badRequest(confirm.render());
        }

        if (user.validated) {
            flash("error", Messages.get("error.account.already.validated"));
            return badRequest(confirm.render());
        }

     	user.confirmationToken = null;
        user.validated = true;
        user.save();
        String subject = Messages.get("mail.welcome.subject");
        String message = Messages.get("mail.welcome.message");
        Mail.Envelope envelope = new Mail.Envelope(subject, message, user.email);
        Mail mailer = new Mail(mailerClient);
        mailer.sendMail(envelope);
        flash("success", Messages.get("account.successfully.validated"));
        return ok(confirm.render());
    }
}
