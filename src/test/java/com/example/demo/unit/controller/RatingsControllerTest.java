package com.example.demo.unit.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.demo.controller.RatingsController;
import com.example.demo.dto.PoliticianDTO;
import com.example.demo.dto.RatingDTO;
import com.example.demo.dtomapper.RatingDtoMapper;
import com.example.demo.dtomapper.interfaces.DTOMapper;
import com.example.demo.model.Politicians;
import com.example.demo.model.PoliticiansRating;
import com.example.demo.model.UserRater;
import com.example.demo.model.enums.PoliticalParty;
import com.example.demo.model.enums.Rating;
import com.example.demo.service.RatingService;

@ExtendWith(SpringExtension.class)
public class RatingsControllerTest {
	
	@Mock
	public RatingService service;
	
	public RatingsController controller;
	
	public Politicians politician;
	public PoliticiansRating politiciansRating;
	public RatingDTO ratingDTO;
	public PoliticianDTO politicianDTO;
	
	@BeforeEach
	public void setup() {
		controller = new RatingsController(service);
		
		politician = new Politicians();
		politician.setId(1);
		politician.setName("Mirriam Defensor");
		politician.setRating(0.00D);
		politician.setTotalRating(0.00D);
		
		politiciansRating = new PoliticiansRating(1, 0.00D, new UserRater("test", PoliticalParty.DDS, "test@gmail.com"), politician);
		
		politicianDTO = new PoliticianDTO("Mirriam Defensor", "1", 0.00D, Rating.LOW);
		
		ratingDTO = new RatingDTO(0.00D, new UserRater("test", PoliticalParty.DDS, "test@gmail.com"), politicianDTO);
	}
	
	@Test
	public void	assertEqualsReturnedDto() {
		when(service.findById("1")).thenReturn(politiciansRating);
		
		ResponseEntity<RatingDTO> response = controller.getRatingById("1");
		
		assertThat(ratingDTO,
				equalTo(response.getBody()));
	}
	
	@Test
	public void	assertEqualsReturnedDtoWithListOfRaters() {
		politician.setId(2);
		politician.setName("Leni Robredo");
		PoliticiansRating politiciansRating2 = new PoliticiansRating(2, 0.00D, new UserRater("test", PoliticalParty.DDS, "test@gmail.com"), politician);
		List<PoliticiansRating> listOfPoliticiansRating = List.of(politiciansRating,politiciansRating2);
		
		DTOMapper<RatingDTO, PoliticiansRating> mapper = new RatingDtoMapper();
		List<RatingDTO> listOfRatingDTO = List.of(mapper.mapToDTO(politiciansRating),mapper.mapToDTO(politiciansRating2));
		
		when(service.findRatingsByFacebookEmail("test@gmail.com")).thenReturn(listOfPoliticiansRating);
		
		ResponseEntity<List<RatingDTO>> response = controller.getRatingByRater("test@gmail.com");
		
		assertThat(listOfRatingDTO,
				equalTo(response.getBody()));
	}

}