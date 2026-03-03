package com.cihbank.backend.team;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TeamServiceTest {
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    TeamService teamService;
    @Test
    void shouldCreateTeam(){
        Team team = new Team();
        team.setName("Monetique");
        team.setDescription("Canal monétique");
        team.setIsActive(true);
        Team teamSaved = teamService.createTeam(team);
        assertThat(teamSaved.getIdTeam()).isNotNull();
    }
    @Test
    void shouldUpdateTeam(){
        Team team = new Team();
        team.setName("Cartes");
        team.setDescription("Cartes refusées");
        team.setIsActive(true);
        Team savedTeam = teamRepository.save(team);
        Team updatedTeam = new Team();
        updatedTeam.setName("E_Commerce");
        updatedTeam.setDescription("Problèmes internet");
        updatedTeam.setIsActive(false);
        Team newTeam = teamService.updateTeam(savedTeam.getIdTeam(), updatedTeam);
        assertThat(newTeam.getName()).isEqualTo("E_Commerce");
        assertThat(newTeam.getDescription()).isEqualTo("Problèmes internet");
        assertThat(newTeam.getIsActive()).isFalse();
    }
    @Test
    void shouldActivateTeam(){
        Team team = new Team();
        team.setName("Monétique");
        team.setDescription("App error");
        team.setIsActive(false);
        Team teamSaved = teamService.createTeam(team);
        assertThat(teamSaved.getIsActive()).isFalse();
        Team activatedTeam = teamService.activateTeam(teamSaved.getIdTeam());
        assertThat(activatedTeam.getIsActive()).isTrue();
    }
    @Test
    void shouldDeactivateTeam(){
        Team team = new Team();
        team.setName("Monétique");
        team.setDescription("App error");
        team.setIsActive(true);
        Team teamSaved = teamService.createTeam(team);
        assertThat(teamSaved.getIsActive()).isTrue();
        Team deactivatedTeam = teamService.deactivateTeam(teamSaved.getIdTeam());
        assertThat(deactivatedTeam.getIsActive()).isFalse();
    }
    @Test
    void shouldGetTeam(){
        Team team = new Team();
        team.setName("Monétique");
        team.setDescription("App error");
        team.setIsActive(true);
        Team savedTeam = teamService.createTeam(team);
        Team result = teamService.getTeam(savedTeam.getIdTeam());
        assertThat(result.getIdTeam()).isNotNull();
        assertThat(result.getName()).isEqualTo("Monétique");
        assertThat(result.getDescription()).isEqualTo("App error");

    }
    @Test
    void shouldGetAllTeam(){
        Team team = new Team();
        team.setName("Monétique");
        team.setDescription("App error");
        team.setIsActive(true);
        Team teamSaved = teamService.createTeam(team);
        Team team2 = new Team();
        team2.setName("Monétical");
        team2.setDescription("Request error");
        team2.setIsActive(true);
        Team teamSaved2 = teamService.createTeam(team2);
        List<Team> listTeam = teamService.getAllTeam();
        assertThat(listTeam).isNotEmpty();
        assertThat(listTeam).hasSize(2);

    }

}
