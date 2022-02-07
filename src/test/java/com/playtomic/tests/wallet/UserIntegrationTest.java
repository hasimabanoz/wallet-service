package com.playtomic.tests.wallet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import lombok.Getter;
import lombok.Setter;

@SpringBootTest
@ActiveProfiles(profiles = "dev")
public class UserIntegrationTest {
	
	@Test
    public void givenAnnotatedUser_thenHasGettersAndSetters() {
        User user = new User();
        user.setFirstName("Test");
        Assertions.assertEquals(user.getFirstName(), "Test");
    }

    @Getter @Setter
    class User {
        private String firstName;
    }
    
}
