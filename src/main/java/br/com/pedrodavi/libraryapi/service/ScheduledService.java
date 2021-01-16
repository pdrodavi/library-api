package br.com.pedrodavi.libraryapi.service;

import br.com.pedrodavi.libraryapi.model.entity.Loan;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduledService {

    private static final String CRON_LATE_LOANS = "0 0 12 1/1 * ?";

    @Value("${application.mail.lateloans.message}")
    private String messageBodyEmail;

    private final LoanService loanService;
    private final EmailService emailService;

    @Scheduled(cron = CRON_LATE_LOANS)
    public void sendMailToLateLoans(){
        List<Loan> allLateLoans = loanService.getAllLateLoans(); // obtem todos empréstimos atrasados
        List<String> emails = allLateLoans.stream().map(loan -> loan.getEmail()).collect(Collectors.toList());
        emailService.sendMails(messageBodyEmail, emails);
    }

}
