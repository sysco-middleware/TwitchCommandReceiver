package no.sysco.cip.controller;

import no.sysco.cip.RoutingApplication;
import no.sysco.cip.model.User;
import no.sysco.cip.model.User.Gender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import javax.ws.rs.NotFoundException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ActiveProfiles("local")
@ExtendWith(SpringExtension.class) //To add backward compatibility for JUnit4 type tests
@SpringBootTest(
  classes = {RoutingApplication.class},
  webEnvironment=WebEnvironment.DEFINED_PORT
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS) //Clears the context cache after each test method
class UserServiceControllerResteasyTest {

  @Autowired
  private UserServiceControllerResteasyTestProxy userServiceProxy;

  @Test
  public void shouldFetchAllUsers() throws Exception {
    assertThat(userServiceProxy).withFailMessage("TEST Proxy not initialized").isNotNull();
    List<User> users = userServiceProxy.fetchUsers("MALE");
    assertThat(users).isNotNull();
    users.forEach(user ->  assertThat(user.getGender()).isEqualTo(Gender.MALE));
  }

  @Test
  public void shouldInsertUser() throws Exception {
    assertThat(userServiceProxy).withFailMessage("TEST Proxy not initialized").isNotNull();
    //Given
    UUID userUid = UUID.randomUUID();
    User user = new User(userUid, "Dalibor", "Blazevic",
      Gender.MALE, 49, "dalibor.blazevic@sysco.no");

//    List<User> users = userServiceProxy.fetchUsers("MALE");
    //When
    userServiceProxy.insertNewUser(user);

    //Then
    User joe = userServiceProxy.fetchUser(userUid);
    assertThat(joe).isEqualToComparingFieldByField(user);
  }
  @Test
  public void shouldDeleteUser() throws Exception {
    //Given
    UUID userUid = UUID.randomUUID();
    User user = new User(userUid, "Joe", "Jones",
      Gender.MALE, 22, "joe.jones@gmail.com");

    //When
    userServiceProxy.insertNewUser(user);

    //Then
    User joe = userServiceProxy.fetchUser(userUid);
    assertThat(joe).isEqualToComparingFieldByField(user);

    //When
    userServiceProxy.deleteUser(userUid);

    //Then
    assertThatThrownBy(() -> userServiceProxy.fetchUser(userUid))
      .isInstanceOf(NotFoundException.class);

  }

  @Test
  public void shouldUpdateUser() throws Exception {
    //Given
    UUID userUid = UUID.randomUUID();
    User user = new User(userUid, "Joe", "Jones",
      Gender.MALE, 22, "joe.jones@gmail.com");

    //When
    userServiceProxy.insertNewUser(user);

    User updatedUser = new User(userUid, "Alex", "Jones",
      Gender.MALE, 55, "alex.jones@gmail.com");

    userServiceProxy.updateUser(updatedUser);

    //Then
    user = userServiceProxy.fetchUser(userUid);
    assertThat(user).isEqualToComparingFieldByField(updatedUser);
  }

  @Test
  public void shouldFetchUsersByGender() throws Exception {
    //Given
    UUID userUid = UUID.randomUUID();

    User user = new User(userUid, "Joe", "Jones",
      Gender.MALE, 22, "joe.jones@gmail.com");

    //When
    userServiceProxy.insertNewUser(user);

    List<User> females = userServiceProxy.fetchUsers(Gender.FEMALE.name());

    assertThat(females).extracting("userUid").doesNotContain(user.getUserUid());
    assertThat(females).extracting("firstName").doesNotContain(user.getFirstName());
    assertThat(females).extracting("lastName").doesNotContain(user.getLastName());
    assertThat(females).extracting("gender").doesNotContain(user.getGender());
    assertThat(females).extracting("age").doesNotContain(user.getAge());
    assertThat(females).extracting("email").doesNotContain(user.getEmail());

    List<User> males = userServiceProxy.fetchUsers(Gender.MALE.name());

    assertThat(males).extracting("userUid").contains(user.getUserUid());
    assertThat(males).extracting("firstName").contains(user.getFirstName());
    assertThat(males).extracting("lastName").contains(user.getLastName());
    assertThat(males).extracting("gender").contains(user.getGender());
    assertThat(males).extracting("age").contains(user.getAge());
    assertThat(males).extracting("email").contains(user.getEmail());

  }
}