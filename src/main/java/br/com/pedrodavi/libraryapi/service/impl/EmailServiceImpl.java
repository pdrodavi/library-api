package br.com.pedrodavi.libraryapi.service.impl;

import br.com.pedrodavi.libraryapi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${application.mail.remetent}")
    private String remetent;

    private final JavaMailSender javaMailSender;

    @Override
    public void sendMails(String messageBodyEmail, List<String> emails) {
        String[] destinations = emails.toArray(new String[emails.size()]); // transformando lista em array string
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(remetent); // remetente
        mailMessage.setSubject("Livro com devolução atrasada!"); // assunto
        mailMessage.setText(messageBodyEmail); // corpo do email
        mailMessage.setTo(destinations); // destinatários
        javaMailSender.send(mailMessage);
    }
}
