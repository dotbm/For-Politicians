package com.example.demo.integration.filters;

import static com.example.demo.baseClasses.AbstractPoliticianControllerTest.withoutRepo;
import static com.example.demo.model.enums.Rating.LOW;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.demo.controller.PoliticianController;
import com.example.demo.dto.PoliticianDTO;
import com.example.demo.dtomapper.PoliticiansDtoMapper;
import com.example.demo.exceptionHandling.GlobalExceptionHandling;
import com.example.demo.filter.AddPoliticianFilter;
import com.example.demo.hateoas.PoliticianAssembler;
import com.example.demo.model.averageCalculator.AverageCalculator;
import com.example.demo.model.entities.Politicians;
import com.example.demo.model.entities.PoliticiansRating;
import com.example.demo.model.entities.Rating;
import com.example.demo.service.PoliticiansService;

@WebMvcTest(controllers = { PoliticianController.class,GlobalExceptionHandling.class })
public class AddPoliticianFilterTest {

	public MockMvc mvc;
	
	@Autowired public WebApplicationContext wac;
	
	@MockBean public PoliticiansService service;
	
	@MockBean public PoliticiansDtoMapper mapper;
	
	@MockBean public PoliticianAssembler assembler;
	
	@Mock public AverageCalculator calculator;
	
	private final String content = """
			{
			    "firstName": "test",
			    "lastName": "name",
			    "rating": 0.01
			}
			""";
	
	private Politicians politician; 
	
	@BeforeEach
	public void setup() {
		politician = withoutRepo
				("test", 
				"name", 
				List.of(new PoliticiansRating()), 
				new Rating(0.01D, 0.01D, calculator));
		
		mvc = MockMvcBuilders.webAppContextSetup(wac)
				.addFilter(new AddPoliticianFilter(), "/api/politicians/politician")
				.alwaysDo(print())
				.build();
	}
	
	@Test 
	public void shouldReturn401AuthorizationRequiredMessageIfAuthorizationIsIncorrect() throws URISyntaxException, Exception {
		when(service.savePolitician(any())).thenReturn(politician);
		
		mvc.perform(post(URI.create("/api/politicians/politician"))
				.content(content)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("err", 
					equalTo("Authorization Required")))
			.andExpect(jsonPath("code", 
					equalTo("401")));
	}
	
	@Test 
	public void shouldReturn401AuthorizationRequiredMessageIfHeaderRequiredIsNull() throws URISyntaxException, Exception {
		when(service.savePolitician(any())).thenReturn(politician);
		
		mvc.perform(post(URI.create("/api/politicians/politician"))
				.content(content)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("err", 
					equalTo("Authorization Required")))
			.andExpect(jsonPath("code", 
					equalTo("401")));
	}
	
	@Test 
	public void shouldReturn201CreatedIfAuthorizationIsCorrect() throws URISyntaxException, Exception {
		var polDTO = new PoliticianDTO("test name", "123polNumber", 0.01D, LOW);
		
		when(service.savePolitician(any())).thenReturn(politician);
		when(assembler.toModel(any(PoliticianDTO.class))).thenReturn(EntityModel.of(polDTO));
		
		mvc.perform(post(URI.create("/api/politicians/politician"))
				.header("Politician-Access", "password")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("name", 
					equalTo("test name")))
			.andExpect(jsonPath("rating", 
					equalTo(0.01)));
	}
	
}
