package com.MovieRateApi.MovieApp.Controller;

import com.MovieRateApi.MovieApp.ML.Favorite;
import com.MovieRateApi.MovieApp.ML.Movie;
import com.MovieRateApi.MovieApp.ML.Result;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/home")
public class MovieController {

    private RestTemplate restTemplate = new RestTemplate();
    private String token = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI1MDRkYmQ3ZWY4NDZkN2EwM2ZiNDg3MTcyNDQwOWRjNCIsIm5iZiI6MTc0NzMzMzUyMy42MjEsInN1YiI6IjY4MjYzMTkzNzFlMzAyM2ZmMWExNzZmYyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.AEpAc1jTU2qBvrc6WDKNH5pJQmLF4VJKzz1aycAO0Vc";
    private int account_id = 22012906;

    @GetMapping("/popular")
    public String DiscoverMovie(@RequestParam(required = false) Integer page, HttpSession session, Model model) {

        if (session.getAttribute("session_id") != null) {

            HttpHeaders httpHeader = new HttpHeaders();
            httpHeader.setBearerAuth(token);
            HttpEntity<String> entity = new HttpEntity<>(httpHeader);

            try {

                ResponseEntity<Result<Movie>> getMovies = restTemplate.exchange("https://api.themoviedb.org/3/movie/popular",
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

    @GetMapping("/addFavoriteMovie/{media_id}")
    public String AddFavoriteMovie(HttpSession session, Model model, @PathVariable int media_id) {
        String sessionId = session.getAttribute("session_id").toString();
        Favorite favorite = new Favorite();

        try {
            if (session.getAttribute("session_id") != null) {
                favorite.setMedia_type("movie");
                favorite.setMedia_id(media_id);
                favorite.setFavorite(true);

                HttpHeaders httpheader = new HttpHeaders();
                httpheader.setBearerAuth(token);
                httpheader.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Favorite> entity = new HttpEntity<>(favorite, httpheader);

                ResponseEntity<Result> response = restTemplate.exchange("https://api.themoviedb.org/3/account/" + account_id + "/favorite?session_id=" + sessionId,
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<Result>() {
                });

                if (response.getStatusCode().is2xxSuccessful()) {
                    return "redirect:/home/popular";
                }
            }
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("status", ex.getStatusCode());
            model.addAttribute("message", ex.getMessage());
            return "Error";
        }
        return "redirect:/home/popular";
    }

    @GetMapping("/detailMovie/{media_id}")
    public String Details(HttpSession session, Model model, @PathVariable int media_id) {
        try {
            if (session.getAttribute("session_id") != null) {

                HttpHeaders httpheader = new HttpHeaders();
                httpheader.setBearerAuth(token);
                HttpEntity<String> entity = new HttpEntity<>(httpheader);

                ResponseEntity<Movie> response = restTemplate.exchange("https://api.themoviedb.org/3/movie/" + media_id,
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<Movie>() {
                });

                model.addAttribute("movie", response.getBody());

                return "Detail";
            }
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("status", ex.getStatusCode());
            model.addAttribute("message", ex.getMessage());
            return "ErrorPage";
        }
        return "Detail";
    }
}
