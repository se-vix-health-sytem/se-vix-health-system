package com.nvivx.vixhealthsystem.controllers.staff;

import com.nvivx.vixhealthsystem.service.DevCredentialStore;
import com.nvivx.vixhealthsystem.service.core.EmployeeService;
import com.nvivx.vixhealthsystem.service.core.PatientService;
import com.nvivx.vixhealthsystem.service.integration.FirebaseAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @class AuthControllerTest
 * @brief Unit tests for AuthController login functionality.
 *
 * These tests verify that the login page is correctly rendered
 * and that error states are properly reflected in the model.
 */
class AuthControllerTest {

    /// Mock MVC instance used to simulate HTTP requests without starting a server.
    private MockMvc mockMvc;

    /// Mocked Firebase authentication service dependency.
    private FirebaseAuthService firebaseAuthService = mock(FirebaseAuthService.class);

    /// Mocked employee service dependency.
    private EmployeeService employeeService = mock(EmployeeService.class);

    /// Mocked patient service dependency.
    private PatientService patientService = mock(PatientService.class);

    /// Mocked development credential store used for login-related data.
    private DevCredentialStore devCredentialStore = mock(DevCredentialStore.class);

    /**
     * @brief Sets up the standalone controller environment before each test.
     *
     * Initializes the AuthController with mocked dependencies and configures
     * MockMvc with a simple internal view resolver for HTML templates.
     */
    @BeforeEach
    void setup() {

        // Create controller with mocked dependencies
        AuthController controller = new AuthController(
                firebaseAuthService,
                employeeService,
                patientService,
                devCredentialStore
        );

        // Configure a basic view resolver to map logical view names to HTML templates
        ViewResolver viewResolver = new InternalResourceViewResolver("/templates/", ".html");

        // Build standalone MockMvc instance for controller testing
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setViewResolvers(viewResolver)
                .build();
    }

    /**
     * @brief Verifies that the login page loads successfully.
     *
     * Ensures:
     * - HTTP status is 200 OK
     * - Correct view name "login" is returned
     */
    @Test
    void loginPage_returnsView() throws Exception {

        // Mock empty dev credentials to avoid null or unexpected model data
        when(devCredentialStore.getAll()).thenReturn(java.util.List.of());

        // Perform GET request to /login and validate response
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    /**
     * @brief Verifies that login page displays an error when error parameter is present.
     *
     * Ensures:
     * - HTTP status is 200 OK
     * - Correct view name "login" is returned
     * - Model contains "error" attribute when login fails
     */
    @Test
    void loginPage_withError_showsErrorMessage() throws Exception {

        // Mock empty dev credentials list
        when(devCredentialStore.getAll()).thenReturn(java.util.List.of());

        // Simulate login error via query parameter
        mockMvc.perform(get("/login").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("error"));
    }
}