package com.eneik.generated.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Given valid phone login, When submitted, Then an account is provisioned")
    void testPhoneLoginProvisionsAccount() throws Exception {
        String json = """
            {
                "phone": "+995599776655",
                "name": "Mariam",
                "city": "Kutaisi"
            }
            """;

        mockMvc.perform(post("/api/profile/phone-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.phone").value("+995599776655"))
            .andExpect(jsonPath("$.name").value("Mariam"))
            .andExpect(jsonPath("$.city").value("Kutaisi"))
            .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    @DisplayName("Given missing phone number, When phone login submitted, Then returns 400 Bad Request")
    void testPhoneLoginMissingPhoneReturnsBadRequest() throws Exception {
        String json = """
            {
                "name": "Mariam"
            }
            """;

        mockMvc.perform(post("/api/profile/phone-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Phone number is required"));
    }

    @Test
    @DisplayName("Given valid FB token, When importing, Then photos and descriptions are fetched")
    void testImportFacebookValidTokenFetchesPhotosAndDescription() throws Exception {
        String json = """
            {
                "facebookToken": "valid-fb-token-12345",
                "facebookPageId": "fb-page-tbilisi-salon",
                "phone": "+995599112233"
            }
            """;

        mockMvc.perform(post("/api/profile/import/facebook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.facebookPageId").value("fb-page-tbilisi-salon"))
            .andExpect(jsonPath("$.description", containsString("Imported Facebook Business Page Description")))
            .andExpect(jsonPath("$.address").value("Chavchavadze Ave 12, Tbilisi"))
            .andExpect(jsonPath("$.photos", hasSize(2)))
            .andExpect(jsonPath("$.photos[0]", containsString("photo1.jpg")));
    }

    @Test
    @DisplayName("Given invalid FB token, When importing, Then returns 400 Bad Request")
    void testImportFacebookInvalidTokenReturnsBadRequest() throws Exception {
        String json = """
            {
                "facebookToken": "invalid-token",
                "facebookPageId": "fb-page-tbilisi-salon"
            }
            """;

        mockMvc.perform(post("/api/profile/import/facebook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Invalid Facebook access token"));
    }
}
