package com.MovieRateApi.MovieApp.Controller;

import com.MovieRateApi.MovieApp.ML.Movie;
import com.MovieRateApi.MovieApp.ML.Result;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/home")
public class MovieController {

    private RestTemplate restTemplate = new RestTemplate();
    private String token = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI1MDRkYmQ3ZWY4NDZkN2EwM2ZiNDg3MTcyNDQwOWRjNCIsIm5iZiI6MTc0NzMzMzUyMy42MjEsInN1YiI6IjY4MjYzMTkzNzFlMzAyM2ZmMWExNzZmYyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.AEpAc1jTU2qBvrc6WDKNH5pJQmLF4VJKzz1aycAO0Vc";

    @GetMapping("/popular")
    public String DiscoverMovie(@RequestParam(required = false) Integer page, HttpSession session, Model model) {

        if (session.getAttribute("session_id") != null) {

            HttpHeaders httpHeader = new HttpHeaders();
            httpHeader.setBearerAuth(token);
            HttpEntity<String> entity = new HttpEntity<>(httpHeader);

            try {

                ResponseEntity<Result<Movie>> getMovies = restTemplate.exchange("https://api.themoviedb.org/3/discover/movie",
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<Result<Movie>>() {
                });

                Result result = getMovies.getBody();

                model.addAttribute("listMovies", result.results);

                return "Index";
            } catch (HttpStatusCodeException ex) {
                model.addAttribute("status", ex.getStatusCode());
                model.addAttribute("message", ex.getMessage());
                return "ErrorPage";
            }
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/addFavorite")
    public String AddFavorite(HttpSession session, Model model) {

        String sessionId = session.getAttribute("session_id").toString();
        String username = session.getAttribute("username").toString();

        try {
            ResponseEntity<Result> response = restTemplate.exchange("https://api.themoviedb.org/3/account/" + username + "/favorite?session_id=" + sessionId,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result>() {
            });
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("status", ex.getStatusCode());
            model.addAttribute("message", ex.getMessage());
            return "Error";
        }
        return "redirect:/home/popular";
    }
}
