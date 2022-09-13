package com.sathyajith.moviecatalogservice.controller;

import com.sathyajith.moviecatalogservice.model.CatalogItem;
import com.sathyajith.moviecatalogservice.model.Movie;
import com.sathyajith.moviecatalogservice.model.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResourceController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){
        //get all rated movie ids
        UserRating ratings = restTemplate.getForObject("http://rating-data-service/ratingsdata/user/"+userId , UserRating.class);

        return ratings.getUserRating().stream().map(rating -> {
            //for each movie id, call movie info service and get details
            Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
            //put then all together
            return new CatalogItem(movie.getName(), "desc", rating.getRating());
        }).collect(Collectors.toList());

    }
}

           /* Movie movie = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/movies/" + rating.getMovieId())
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();*/
