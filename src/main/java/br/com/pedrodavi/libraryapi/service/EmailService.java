package br.com.pedrodavi.libraryapi.service;

import java.util.List;

public interface EmailService {
    void sendMails(String messageBodyEmail, List<String> emails);
}