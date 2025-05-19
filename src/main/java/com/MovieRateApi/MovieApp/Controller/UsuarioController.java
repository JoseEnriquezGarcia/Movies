package com.MovieRateApi.MovieApp.Controller;

import com.MovieRateApi.MovieApp.DTO.SessionDTO;
import com.MovieRateApi.MovieApp.DTO.TokenDTO;
import com.MovieRateApi.MovieApp.ML.ResultLogin;
import com.MovieRateApi.MovieApp.ML.Usuario;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/login")
public class UsuarioController {
    private RestTemplate restTemplate = new RestTemplate();
    private String token = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI1MDRkYmQ3ZWY4NDZkN2EwM2ZiNDg3MTcyNDQwOWRjNCIsIm5iZiI6MTc0NzMzMzUyMy42MjEsInN1YiI6IjY4MjYzMTkzNzFlMzAyM2ZmMWExNzZmYyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.AEpAc1jTU2qBvrc6WDKNH5pJQmLF4VJKzz1aycAO0Vc";

    @GetMapping
    public String Login(@ModelAttribute Usuario usuario, Model model) {
        model.addAttribute("usuario", usuario);
        return "Login";
    }

    @PostMapping("/authLogin")
    public String AuthLogin(@ModelAttribute Usuario usuario, Model model, HttpSession session) {
        try {
            //Obtener request_token
            HttpHeaders httpHeaderToken = new HttpHeaders();
            httpHeaderToken.setBearerAuth(token);
            HttpEntity<String> entity = new HttpEntity<>(httpHeaderToken);

            ResponseEntity<ResultLogin> responseToken = restTemplate.exchange("https://api.themoviedb.org/3/authentication/token/new",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<ResultLogin>() {
            });

            if (responseToken.getStatusCode().is2xxSuccessful()) {
                //Validar con login (username, password y request_token)
                HttpHeaders httpHeadersSession = new HttpHeaders();
                httpHeadersSession.setBearerAuth(token);

                usuario.setRequest_token(responseToken.getBody().request_token);

                HttpEntity<Usuario> entityCreateSession = new HttpEntity(usuario, httpHeadersSession);
                ResponseEntity<ResultLogin> responseCreateSession = restTemplate.exchange("https://api.themoviedb.org/3/authentication/token/validate_with_login",
                        HttpMethod.POST,
                        entityCreateSession,
                        new ParameterizedTypeReference<ResultLogin>() {
                });

                if (responseCreateSession.getStatusCode().is2xxSuccessful()) {
                    //Crear sesión con request_token
                    String validatedToken = responseCreateSession.getBody().request_token;

                    HttpHeaders httpheaderValidateToken = new HttpHeaders();
                    httpheaderValidateToken.setBearerAuth(token);
                    httpheaderValidateToken.setContentType(MediaType.APPLICATION_JSON);

                    //DTO Por que se utiliza para transportar el token validado
                    TokenDTO tokenValid = new TokenDTO(validatedToken);
                    HttpEntity<TokenDTO> entityValidateToken = new HttpEntity<>(tokenValid, httpheaderValidateToken);

                    ResponseEntity<ResultLogin> responseValidateToken = restTemplate.exchange("https://api.themoviedb.org/3/authentication/session/new",
                            HttpMethod.POST,
                            entityValidateToken,
                            new ParameterizedTypeReference<ResultLogin>() {
                    });

                    if (responseValidateToken.getStatusCode().is2xxSuccessful()) {
                        String sessionId = responseValidateToken.getBody().session_id;
                        session.setAttribute("session_id", sessionId);
                    }
                }
            }
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("status", ex.getStatusCode());
            model.addAttribute("message", ex.getMessage());
            return "ErrorPage";
        }
        return "redirect:/home/popular";
    }

    @GetMapping("/logout")
    public String DeleteSession(HttpSession session, Model model) {
        String sessionId = session.getAttribute("session_id").toString();

        try {
            HttpHeaders httpHeader = new HttpHeaders();
            httpHeader.setBearerAuth(token);
            httpHeader.setContentType(MediaType.APPLICATION_JSON);
            
            SessionDTO sessionDTO = new SessionDTO(sessionId);
            HttpEntity<SessionDTO> entity = new HttpEntity<>(sessionDTO, httpHeader);
            
            ResponseEntity<ResultLogin> deleteSession = restTemplate.exchange("https://api.themoviedb.org/3/authentication/session",
                    HttpMethod.DELETE,
                    entity,
                    new ParameterizedTypeReference<ResultLogin>() {
            });
            if (deleteSession.getStatusCode().is2xxSuccessful()) {
                session.invalidate();
                return "redirect:/login";
            }
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("status", ex.getStatusCode());
            model.addAttribute("message", ex.getMessage());
            return "ErrorPage";
        }
        return "Index";
    }
}
