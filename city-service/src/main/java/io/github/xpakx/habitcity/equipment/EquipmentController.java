package io.github.xpakx.habitcity.equipment;

import io.github.xpakx.habitcity.equipment.dto.EquipmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EquipmentController {
    private final EquipmentService service;

    @GetMapping("/equipment")
    public ResponseEntity<EquipmentResponse> getEquipment(@RequestHeader String id) {
        return new ResponseEntity<>(
                service.getEquipment(Long.valueOf(id)),
                HttpStatus.OK
        );
    }
}
