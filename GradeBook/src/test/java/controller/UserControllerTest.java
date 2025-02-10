package controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    @Test
    void registerValidation() {
        UserController userController = new UserController(null);
        String result = userController.registerValidation("username", "password");
        assertNull(result);

        result = userController.registerValidation("", "password");
        assertEquals("Username and password cannot be empty.", result);

        result = userController.registerValidation("username", "");
        assertEquals("Username and password cannot be empty.", result);

        result = userController.registerValidation("us", "password");
        assertEquals("Username must be between 3 and 20 characters.", result);

        result = userController.registerValidation("username", "pass");
        assertEquals("Password must be between 6 and 20 characters.", result);

        result = userController.registerValidation("user@name", "password");
        assertEquals("Username can only contain letters and numbers.", result);

        result = userController.registerValidation("username", "pass^word");
        assertEquals("Password can only contain letters, numbers, \nand special characters.", result);
    }
}