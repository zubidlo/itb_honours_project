package controllers.account.settings;

import controllers.Secured;
import models.Token;
import models.User;
import play.Logger;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.account.settings.password;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;

import javax.inject.Inject;

import java.net.MalformedURLException;

@Security.Authenticated(Secured.class)
public class Password extends Controller {
    @Inject
    MailerClient mailerClient;

    public Result index() {
        return ok(password.render(User.findByEmail(request().username())));
    }

    public Result runPassword() {
        User user = User.findByEmail(request().username());
        try {
            Token t = new Token();
            t.sendMailResetPassword(user,mailerClient);
            flash("success", Messages.get("resetpassword.mailsent"));
            return ok(password.render(user));
        } catch (MalformedURLException e) {
            Logger.error("Cannot validate URL", e);
            flash("error", Messages.get("error.technical"));
        }
        return badRequest(password.render(user));
    }
}
