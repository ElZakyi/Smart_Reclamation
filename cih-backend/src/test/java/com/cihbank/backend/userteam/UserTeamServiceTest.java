package com.cihbank.backend.userteam;

import com.cihbank.backend.team.Team;
import com.cihbank.backend.team.TeamService;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class UserTeamServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private UserTeamService userTeamService;
    @Autowired
    private UserTeamRepository userTeamRepository;
    @Test
    void shouldAssignUserToTeam(){
        User user = new User();
        user.setFullName("Zakaria");
        user.setEmail("zak@cih.com");
        user.setPassword("1234");
        user.setPasswordHash("DummyHash");
        user.setIsActive(true);
        User savedUser = userService.createUser(user);
        Team team = new Team();
        team.setName("Monetical");
        team.setDescription("Error request");
        team.setIsActive(true);
        Team savedTeam = teamService.createTeam(team);
        userTeamService.assignUserToTeam(savedTeam.getIdTeam(),savedUser.getIdUser());
        List<UserTeam> members = userTeamRepository.findAll();
        Assertions.assertThat(members).isNotEmpty();
        Assertions.assertThat(members).hasSize(1);
        Assertions.assertThat(members.get(0).getTeam().getName()).isEqualTo("Monetical");

    }
    @Test
    void shouldRemoveUserFromTeam(){
        User user = new User();
        user.setFullName("Zakaria");
        user.setEmail("zak@cih.com");
        user.setPassword("1234");
        user.setPasswordHash("DummyHash");
        user.setIsActive(true);
        User savedUser = userService.createUser(user);
        Team team = new Team();
        team.setName("Monetical");
        team.setDescription("Error request");
        team.setIsActive(true);
        Team savedTeam = teamService.createTeam(team);
        userTeamService.assignUserToTeam(savedTeam.getIdTeam(),savedUser.getIdUser());
        List<UserTeam> members = userTeamRepository.findAll();
        Assertions.assertThat(members).isNotEmpty();
        userTeamService.removeUserFromTeam(savedTeam.getIdTeam(),savedUser.getIdUser());
        List<UserTeam> membersAfterRemoved = userTeamRepository.findAll();
        Assertions.assertThat(membersAfterRemoved).isEmpty();

    }
    @Test
    void shouldReturnMembersOfTeam(){
        User user = new User();
        user.setFullName("Zakaria");
        user.setEmail("zak@cih.com");
        user.setPassword("1234");
        user.setPasswordHash("DummyHash");
        user.setIsActive(true);
        User savedUser = userService.createUser(user);
        User user2 = new User();
        user2.setFullName("saad");
        user2.setEmail("saad@cih.com");
        user2.setPassword("1234");
        user2.setPasswordHash("DummyHash");
        user2.setIsActive(true);
        User savedUser2 = userService.createUser(user2);
        Team team = new Team();
        team.setName("Monetical");
        team.setDescription("Error request");
        team.setIsActive(true);
        Team savedTeam = teamService.createTeam(team);
        userTeamService.assignUserToTeam(savedTeam.getIdTeam(),savedUser.getIdUser());
        userTeamService.assignUserToTeam(savedTeam.getIdTeam(),savedUser2.getIdUser());
        List<UserTeam> members = userTeamService.getMembersOfTeam(savedTeam.getIdTeam());
        Assertions.assertThat(members).isNotEmpty();
        Assertions.assertThat(members).hasSize(2);


    }
    @Test
    void shouldReturnTeamsOfUser(){
        User user = new User();
        user.setFullName("Zakaria");
        user.setEmail("zak@cih.com");
        user.setPassword("1234");
        user.setPasswordHash("DummyHash");
        user.setIsActive(true);
        User savedUser = userService.createUser(user);
        User user2 = new User();
        user2.setFullName("saad");
        user2.setEmail("saad@cih.com");
        user2.setPassword("1234");
        user2.setPasswordHash("DummyHash");
        user2.setIsActive(true);
        User savedUser2 = userService.createUser(user2);
        Team team = new Team();
        team.setName("Monetical");
        team.setDescription("Error request");
        team.setIsActive(true);
        Team savedTeam = teamService.createTeam(team);
        userTeamService.assignUserToTeam(savedTeam.getIdTeam(),savedUser.getIdUser());
        userTeamService.assignUserToTeam(savedTeam.getIdTeam(),savedUser2.getIdUser());
        List<UserTeam> members = userTeamService.getTeamsOfUser(savedUser.getIdUser());
        Assertions.assertThat(members).isNotEmpty();
        Assertions.assertThat(members).hasSize(1);
        List<UserTeam> members2 = userTeamService.getTeamsOfUser(savedUser2.getIdUser());
        Assertions.assertThat(members2).isNotEmpty();
        Assertions.assertThat(members2).hasSize(1);


    }

}
