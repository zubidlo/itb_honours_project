package models.utils;

import play.Configuration;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;

import javax.inject.Inject;

import play.libs.Akka;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Mail {
    private final MailerClient mailerClient;
    private static final int DELAY = 1;

    public Mail(MailerClient mc) {
        mailerClient = mc;
    }

    public final static class Envelope {
        String subject;
        public String message;
        final List<String> toEmails;

        public Envelope(String sub, String msg, List<String> te) {
            subject = sub;
            message = msg;
            toEmails = te;
        }

        public Envelope(String sub, String msg, String email) {
            message = msg;
            subject = sub;
            toEmails = new ArrayList();
            toEmails.add(email);
        }
    }

    public void sendMail(Envelope envelope) {
        final Job job = new Job(envelope, mailerClient);
        final FiniteDuration delay = Duration.create(DELAY, TimeUnit.SECONDS);
        Akka.system().scheduler().scheduleOnce(delay, job, Akka.system().dispatcher());
    }

    private static final class Job implements Runnable {
        final MailerClient mailerClient;
        final Envelope envelope;

        @Inject
        public Job(Envelope env, MailerClient mc) {
            envelope = env;
            mailerClient = mc;
        }

        public void run() {
            final String mailFrom = Configuration.root().getString("mail.from");
            final String mailSign = Configuration.root().getString("mail.sign");
            final Email email = new Email()
                    .setFrom(mailFrom)
                    .setSubject(envelope.subject)
                    .setBodyText(envelope.message + "\n\n " + mailSign)
                    .setBodyHtml(envelope.message + "<br><br>--<br>" + mailSign)
                    .setTo(envelope.toEmails);

            mailerClient.send(email);
        }
    }
}
