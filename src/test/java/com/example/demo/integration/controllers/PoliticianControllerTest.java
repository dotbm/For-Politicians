package com.example.demo.integration.controllers;

import static java.net.URI.create;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.MediaTypes.HAL_FORMS_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.demo.controller.PoliticianController;
import com.example.demo.dto.PoliticianDTO;
import com.example.demo.dtomapper.PoliticiansDtoMapper;
import com.example.demo.hateoas.PoliticianAssembler;
import com.example.demo.model.averageCalculator.LowSatisfactionAverageCalculator;
import com.example.demo.model.entities.Politicians;
import com.example.demo.model.entities.PoliticiansRating;
import com.example.demo.model.entities.Rating;
import com.example.demo.service.PoliticiansService;

@WebMvcTest(PoliticianController.class)
public class PoliticianControllerTest {

	MockMvc mvc;

	@MockBean PoliticiansService polService;
	@MockBean PoliticianAssembler assembler;
	
	Politicians politician;
	PoliticiansRating politiciansRating;
	PoliticianDTO politicianDTO;
	EntityModel<PoliticianDTO> entityModel; 
	
	@BeforeEach
	public void setup(WebApplicationContext wac) {
		this.mvc = MockMvcBuilders.webAppContextSetup(wac)
				.alwaysDo(print())
				.build();
		
		politician = new Politicians.PoliticiansBuilder("123polNumber")
			.setId(123)
			.setFirstName("Mirriam")
			.setLastName("Defensor")
			.setPoliticianNumber("123polNumber")
			.setRating(new Rating(1D, 1D, mock(LowSatisfactionAverageCalculator.class)))
			.build();
			
		
		politicianDTO = new PoliticiansDtoMapper().mapToDTO(politician);
		
		entityModel = new PoliticianAssembler().toModel(politicianDTO);
	}
	
	@Test
	public void shouldEqualDtoOutputsByPoliticianNumber() throws Exception {
		when(polService.findPoliticianByNumber("123polNumber")).thenReturn(politician);
		when(assembler.toModel(any())).thenReturn(entityModel);
		
		this.mvc.perform(get(create("/api/politicians/politician/123polNumber"))
				.accept(HAL_FORMS_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(HAL_FORMS_JSON))
				.andExpect(jsonPath("name", equalTo(politicianDTO.getName())))
				.andExpect(jsonPath("id", equalTo(politicianDTO.getId())))
				.andExpect(jsonPath("rating", equalTo(1.0D)))
				.andExpect(jsonPath("satisfaction_rate", equalTo(politicianDTO.getSatisfactionRate().toString())));
	}
	
	@Test
	public void shouldEqualDtoOutputsWhenSaved() throws Exception {
		when(polService.savePolitician(any())).thenReturn(politician);
		when(assembler.toModel(any())).thenReturn(entityModel);
		
		String content = """
				{
					"firstName": "Test",
					"lastName": "Name",
					"rating": 1.00
				}
				""";
		
		this.mvc.perform(post(create("/api/politicians/politician/"))
				.content(content)
				.contentType(APPLICATION_JSON)
				.accept(HAL_FORMS_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(HAL_FORMS_JSON))
				.andExpect(jsonPath("name", equalTo(politicianDTO.getName())))
				.andExpect(jsonPath("id", equalTo(politicianDTO.getId())))
				.andExpect(jsonPath("rating", equalTo(1.0D)))
				.andExpect(jsonPath("satisfaction_rate", equalTo(politicianDTO.getSatisfactionRate().toString())));
	}
	
}
