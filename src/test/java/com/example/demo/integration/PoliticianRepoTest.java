package com.example.demo.integration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Commit;

import com.example.demo.model.averageCalculator.AverageCalculator;
import com.example.demo.model.entities.Politicians;
import com.example.demo.model.entities.Rating;
import com.example.demo.repository.PoliticiansRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PoliticianRepoTest {

	@Autowired
	private PoliticiansRepository repo;
	
	@Mock
	public AverageCalculator calculator;
	
	@Test
	@Order(1)
	@Commit
	public void shouldBeEqualOnSavedEntity() {
		var politicianToBeSaved = new Politicians();
		politicianToBeSaved.setFirstName("Rodrigo");
		politicianToBeSaved.setLastName("Duterte");
		politicianToBeSaved.calculateFullName();
		politicianToBeSaved.setRating(new Rating(0.01D, 0.01D, calculator));
		politicianToBeSaved.setPoliticianNumber("1111");
		
		Politicians politician = repo.save(politicianToBeSaved);
		
		assertThat(politicianToBeSaved.getFirstName(), 
				equalTo(politician.getFirstName()));
		assertThat(politicianToBeSaved.getLastName(), 
				equalTo(politician.getLastName()));
		assertThat(politicianToBeSaved.getRating(), 
				equalTo(politician.getRating()));
		assertThat(politicianToBeSaved.getPoliticianNumber(), 
				equalTo(politician.getPoliticianNumber()));
	}
	
	@Test
	@Order(2)
	public void shouldThrowDataIntegrityException() {
		var politicianToBeSaved = new Politicians();
		politicianToBeSaved.setFirstName("Rodrigo");
		politicianToBeSaved.setLastName("Duterte");
		politicianToBeSaved.calculateFullName();
		politicianToBeSaved.setRating(new Rating(0.01D, 0.01D, calculator));
		politicianToBeSaved.setPoliticianNumber("1111");
		
		assertThrows(DataIntegrityViolationException.class,
				() -> repo.saveAndFlush(politicianToBeSaved));
	}
	
}
