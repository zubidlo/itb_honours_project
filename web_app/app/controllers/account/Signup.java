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
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;

import javax.inject.Inject;

import views.html.account.signup.confirm;
import views.html.account.signup.create;
import views.html.account.signup.created;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static play.data.Form.form;

public class Signup extends Controller {
    @Inject
    MailerClient mailerClient;

    public Result create() {
        return ok(create.render(form(Application.Register.class)));
    }

    public Result createFormOnly() {
        return ok(create.render(form(Application.Register.class)));
    }

    public Result save() {
        Form<Application.Register> registerForm = form(Application.Register.class).bindFromRequest();

        if (registerForm.hasErrors()) {
            return badRequest(create.render(registerForm));
        }

        Application.Register register = registerForm.get();
        Result resultError = checkBeforeSave(registerForm, register.email);

        if (resultError != null) {
            return resultError;
        }

        try {
            User user = new User();
            user.email = register.email;
            user.fullname = register.fullname;
            user.passwordHash = Hash.createPassword(register.inputPassword);
            user.confirmationToken = UUID.randomUUID().toString();

            user.save();
            sendMailAskForConfirmation(user);

            return ok(created.render());
        } catch (EmailException e) {
            Logger.debug("Signup.save Cannot send email", e);
            flash("error", Messages.get("error.sending.email"));
        } catch (Exception e) {
            Logger.error("Signup.save error", e);
            flash("error", Messages.get("error.technical"));
        }
        return badRequest(create.render(registerForm));
    }

    private Result checkBeforeSave(Form<Application.Register> registerForm, String email) {
        if (User.findByEmail(email) != null) {
            flash("error", Messages.get("error.email.already.exist"));
            return badRequest(create.render(registerForm));
        }

        return null;
    }

    private void sendMailAskForConfirmation(User user) throws EmailException, MalformedURLException {
        String subject = Messages.get("mail.confirm.subject");

        String urlString = "http://" + Configuration.root().getString("server.hostname");
        urlString += "/confirm/" + user.confirmationToken;
        URL url = new URL(urlString); // validate the URL, will throw an exception if bad.
        String message = Messages.get("mail.confirm.message", url.toString());

        Mail.Envelop envelop = new Mail.Envelop(subject, message, user.email);
        Mail mailer = new Mail(mailerClient);
        mailer.sendMail(envelop);
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

        try {
            if (User.confirm(user)) {
                sendMailConfirmation(user);
                flash("success", Messages.get("account.successfully.validated"));
                return ok(confirm.render());
            } else {
                Logger.debug("Signup.confirm cannot confirm user");
                flash("error", Messages.get("error.confirm"));
                return badRequest(confirm.render());
            }
        } catch (AppException e) {
            Logger.error("Cannot signup", e);
            flash("error", Messages.get("error.technical"));
        } catch (EmailException e) {
            Logger.debug("Cannot send email", e);
            flash("error", Messages.get("error.sending.confirm.email"));
        }
        return badRequest(confirm.render());
    }

    private void sendMailConfirmation(User user) throws EmailException {
        String subject = Messages.get("mail.welcome.subject");
        String message = Messages.get("mail.welcome.message");
        Mail.Envelop envelop = new Mail.Envelop(subject, message, user.email);
        Mail mailer = new Mail(mailerClient);
        mailer.sendMail(envelop);
    }
}
