package br.com.pedrodavi.libraryapi;

import br.com.pedrodavi.libraryapi.service.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class LibraryApiApplication {

//	/** envio teste de email quando subir aplicação */
//	@Autowired
//	private EmailService emailService;

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

//	/** envia o email assim que a aplicação subir */
//	@Bean
//	public CommandLineRunner runner(){
//		return args -> {
//			List<String> emails = Arrays.asList("libraryapi-ead84b@inbox.mailtrap.io");
//			emailService.sendMails("Teste de envio de email.", emails);
//			System.out.println("Email teste enviado!");
//		};
//	}

	public static void main(String[] args) {
		SpringApplication.run(LibraryApiApplication.class, args);
	}

}
