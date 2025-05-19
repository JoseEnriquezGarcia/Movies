package com.MovieRateApi.MovieApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class TokenDTO {
    @Getter @Setter
    private String request_token;
}
