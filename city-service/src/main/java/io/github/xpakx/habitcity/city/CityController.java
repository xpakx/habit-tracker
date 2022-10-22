package io.github.xpakx.habitcity.city;

import io.github.xpakx.habitcity.city.dto.BuildingRequest;
import io.github.xpakx.habitcity.city.dto.BuildingResponse;
import io.github.xpakx.habitcity.city.dto.CityBuildingDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CityController {
    private final CityService service;

    @GetMapping("/city/all")
    public ResponseEntity<List<City>> getCities(@RequestHeader String id) {
        return new ResponseEntity<>(
                service.getCities(Long.valueOf(id)),
                HttpStatus.OK
        );
    }

    @GetMapping("/city/{cityId}/building")
    public ResponseEntity<List<CityBuildingDetails>> getBuildings(@RequestHeader String id, @PathVariable Long cityId) {
        return new ResponseEntity<>(
                service.getBuildings(cityId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }

    @PostMapping("/city/{cityId}/building")
    public ResponseEntity<BuildingResponse> build(@RequestBody BuildingRequest request, @PathVariable Long cityId, @RequestHeader String id) {
        return new ResponseEntity<>(
                service.build(request, cityId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }
}
