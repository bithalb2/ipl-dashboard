package com.subhra.ipldashboard.data;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

import com.subhra.ipldashboard.model.Match;

public class MatchItemProcessor implements ItemProcessor<MatchInput, Match> {

  private static final Logger log = LoggerFactory.getLogger(MatchItemProcessor.class);

  @Override
  public Match process(final MatchInput matchInput) throws Exception {
	  Match match = new Match();
	  match.setId(Long.parseLong(matchInput.getId()));
	  match.setCity(matchInput.getCity());
	  match.setDate(LocalDate.parse(matchInput.getDate()));
	  match.setPlayerOfMatch(matchInput.getPlayerOfMatch());
	  match.setVenue(matchInput.getVenue());
	  
	  String firstInningsTeam, secondInningsTeam;
	  
	  if(matchInput.getTossDecision().equals("bat")) {
		  firstInningsTeam = matchInput.getTossWinner();
		  secondInningsTeam = matchInput.getTossWinner().equals(matchInput.getTeam1()) 
				  							? matchInput.getTeam2() : matchInput.getTeam1();
	  } else {
		  secondInningsTeam = matchInput.getTossWinner();
		  firstInningsTeam = matchInput.getTossWinner().equals(matchInput.getTeam1()) 
				  							? matchInput.getTeam2() : matchInput.getTeam1();
	  }
	  
	  match.setTeam1(firstInningsTeam);
	  match.setTeam2(secondInningsTeam);
	  match.setTossWinner(matchInput.getTossWinner());
	  match.setTossDecision(matchInput.getTossDecision());
	  match.setWinner(matchInput.getWinner());
	  match.setResult(matchInput.getResult());
	  match.setResultMargin(matchInput.getResultMargin());
	  match.setUmpire1(matchInput.getUmpire1());
	  match.setUmpire2(matchInput.getUmpire2());
	  
	  log.info("Converting (" + matchInput + ") into (" + match + ")");
	  
	  return match;
  }

}