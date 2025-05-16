package com.MovieRateApi.MovieApp.Controller;

import com.MovieRateApi.MovieApp.ML.Result;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/login")
public class Login {
    
    private RestTemplate restTemplate = new RestTemplate();
    private String token = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI1MDRkYmQ3ZWY4NDZkN2EwM2ZiNDg3MTcyNDQwOWRjNCIsIm5iZiI6MTc0NzMzMzUyMy42MjEsInN1YiI6IjY4MjYzMTkzNzFlMzAyM2ZmMWExNzZmYyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.AEpAc1jTU2qBvrc6WDKNH5pJQmLF4VJKzz1aycAO0Vc";
    @GetMapping
    public String GetBarerToken(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);
        
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Result> response = restTemplate.exchange("https://api.themoviedb.org/3/authentication/token/new", HttpMethod.GET, entity, new ParameterizedTypeReference<Result>() {
        });
        
        return "Login";
    }
    
    public String Login(){
        
        
    }
}
