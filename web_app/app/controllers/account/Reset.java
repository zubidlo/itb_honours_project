package controllers.account;

import models.Token;
import models.User;
import models.utils.AppException;
import models.utils.Mail;

import org.apache.commons.mail.EmailException;

import controllers.routes;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.account.reset.ask;
import views.html.account.settings.*;
import play.libs.mailer.MailerClient;

import javax.inject.Inject;

import views.html.account.reset.reset;
import views.html.account.reset.runAsk;

import java.net.MalformedURLException;

import static play.data.Form.form;

public class Reset extends Controller {
    @Inject
    MailerClient mailerClient;

    public static class AskForm {
        @Constraints.Required
        public String email;
    }

    public static class ResetForm {
        @Constraints.Required
        public String inputPassword;
    }

    public Result ask() {
        Form<AskForm> askForm = form(AskForm.class);
        return ok(ask.render(askForm));
    }

    public Result runAsk() {
        Form<AskForm> askForm = form(AskForm.class).bindFromRequest();

        if (askForm.hasErrors()) {
            flash("error", Messages.get("signup.valid.email"));
            return badRequest(ask.render(askForm));
        }

        final String email = askForm.get().email;
        Logger.debug("runAsk: email = " + email);
        User user = User.findByEmail(email);
        Logger.debug("runAsk: user = " + user);

        if (user == null) {
            Logger.debug("No user found with email " + email);
            sendFailedPasswordResetAttempt(email);
            return ok(runAsk.render());
        }

        Logger.debug("Sending password reset link to user " + user);

        try {
            Token t = new Token();
            t.sendMailResetPassword(user,mailerClient);
            return ok(runAsk.render());
        } catch (MalformedURLException e) {
            Logger.error("Cannot validate URL", e);
            flash("error", Messages.get("error.technical"));
        }
        return badRequest(ask.render(askForm));
    }

    private void sendFailedPasswordResetAttempt(String email) {
        String subject = Messages.get("mail.reset.fail.subject");
        String message = Messages.get("mail.reset.fail.message", email);

        Mail.Envelope envelope = new Mail.Envelope(subject, message, email);
        Mail mailer = new Mail(mailerClient);
        mailer.sendMail(envelope);

    }

    public Result reset(String token) {

        if (token == null) {
            flash("error", Messages.get("error.technical"));
            Form<AskForm> askForm = form(AskForm.class);
            return badRequest(ask.render(askForm));
        }

        Token resetToken = Token.findByTokenAndType(token, Token.TypeToken.password);
        if (resetToken == null) {
            flash("error", Messages.get("error.technical"));
            Form<AskForm> askForm = form(AskForm.class);
            return badRequest(ask.render(askForm));
        }

        if (resetToken.isExpired()) {
            resetToken.delete();
            flash("error", Messages.get("error.expiredresetlink"));
            Form<AskForm> askForm = form(AskForm.class);
            return badRequest(ask.render(askForm));
        }

        Form<ResetForm> resetForm = form(ResetForm.class);
        return ok(reset.render(resetForm, token));
    }

    public Result runReset(String token) {
        Form<ResetForm> resetForm = form(ResetForm.class).bindFromRequest();

        if (resetForm.hasErrors()) {
            flash("error", Messages.get("signup.valid.password"));
            return badRequest(reset.render(resetForm, token));
        }

        try {
            Token resetToken = Token.findByTokenAndType(token, Token.TypeToken.password);
            if (resetToken == null) {
                flash("error", Messages.get("error.technical"));
                return badRequest(reset.render(resetForm, token));
            }

            if (resetToken.isExpired()) {
                resetToken.delete();
                flash("error", Messages.get("error.expiredresetlink"));
                return badRequest(reset.render(resetForm, token));
            }

            User user = User.FIND.byId(resetToken.userId);
            if (user == null) {
                flash("error", Messages.get("error.technical"));
                return badRequest(reset.render(resetForm, token));
            }

            String password = resetForm.get().inputPassword;
            user.changePassword(password);

            sendPasswordChanged(user);
            flash("success", Messages.get("resetpassword.success"));
            return redirect(routes.Dashboard.index());
        } catch (AppException e) {
            flash("error", Messages.get("error.technical"));
            return badRequest(reset.render(resetForm, token));
        } catch (EmailException e) {
            flash("error", Messages.get("error.technical"));
            return badRequest(reset.render(resetForm, token));
        }

    }

    private void sendPasswordChanged(User user) throws EmailException {
        String subject = Messages.get("mail.reset.confirm.subject");
        String message = Messages.get("mail.reset.confirm.message");
        Mail.Envelope envelope = new Mail.Envelope(subject, message, user.email);
        Mail mailer = new Mail(mailerClient);
        mailer.sendMail(envelope);
    }
}
