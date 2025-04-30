package com.example.RunAgainstTheWind.repositoryTesting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.RunAgainstTheWind.externalApi.route.model.Route;
import com.example.RunAgainstTheWind.externalApi.route.repository.RouteRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RouteRepositoryTest {
    
    @Autowired
    private RouteRepository routeRepository;

    @BeforeEach
    public void setUp() {
        routeRepository.deleteAll();
    }

    @Test
    public void testCreateRoute() {
        // Create new route
        Route route = new Route(10.0);
        routeRepository.save(route);

        // Check if it exists
        assertNotNull(route.getRouteId());

        // Check if it is retrievable from the database
        Route retrievedRoute = routeRepository.findById(route.getRouteId()).get();
        assertNotNull(retrievedRoute);

        // Check if the attributes are correct
        assertEquals(10.0, retrievedRoute.getDistance());
    }

}
