package com.subhra.ipldashboard.data;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.subhra.ipldashboard.model.Team;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	private final EntityManager entityManager;

	@Autowired
	public JobCompletionNotificationListener(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	@Transactional
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! JOB FINISHED! Time to verify the results");

		Map<String, Team> teamData = new HashMap<>();
		
		entityManager.createQuery("select m.team1, count(*) from Match m group by m.team1", Object[].class)
		.getResultList()
		.stream()
		.map(e -> new Team((String) e[0], (long) e[1]))
		.forEach(team -> teamData.put(team.getTeamName(), team));
		
		entityManager.createQuery("select m.team2, count(*) from Match m group by m.team2", Object[].class)
		.getResultList()
		.stream()
		.forEach(e -> {
			Team team = teamData.get((String) e[0]);
			team.setTotalMatches(team.getTotalMatches() + (long) e[1]);
		});
		
		entityManager.createQuery("select m.winner, count(*) from Match m group by m.winner", Object[].class)
		.getResultList()
		.stream()
		.forEach(e -> {
			Team team = teamData.get((String) e[0]);
			if(team != null)
				team.setTotalWins((long) e[1]);
		});
		
		teamData.values().forEach(team -> entityManager.persist(team));
		teamData.values().forEach(team -> System.out.println(team.getTeamName() + " Win Percentage " + (double) (team.getTotalWins() * 100) / team.getTotalMatches()));
		
/*		jdbcTemplate.query("SELECT team1, team2, date, winner, result, result_margin FROM match",
				(rs, row) -> "On " + rs.getString(3) + " match played between " + rs.getString(1) + " VS " + rs.getString(2) +
					" and " + rs.getString(4) + " won the match by " + rs.getString(6) + " " + rs.getString(5))
		.forEach(str -> System.out.println(str));
*/
		}
	}
}