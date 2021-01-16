package br.com.pedrodavi.libraryapi.api.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanFilterDTO {
    private String isbn;
    private String customer;
}
