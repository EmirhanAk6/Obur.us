package com.project.Obur.us.controller;

import com.project.Obur.us.service.PlaceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PlaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlaceService placeService;

    @Test
    void testGetNearbyPlaces() throws Exception {
        when(placeService.findNearbyPlaces(anyDouble(), anyDouble(), anyDouble(), anyInt()))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/places")
                        .param("lat", "41.04")
                        .param("lng", "29.02")
                        .param("radius", "1500")
                        .param("limit", "50")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetNearbyPlacesWithInvalidParams() throws Exception {
        mockMvc.perform(get("/api/v1/places")
                        .param("lat", "invalid")
                        .param("lng", "29.02")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}



