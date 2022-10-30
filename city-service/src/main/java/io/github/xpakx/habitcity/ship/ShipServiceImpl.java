package io.github.xpakx.habitcity.ship;

import io.github.xpakx.habitcity.city.City;
import io.github.xpakx.habitcity.city.CityRepository;
import io.github.xpakx.habitcity.city.error.CityNotFoundException;
import io.github.xpakx.habitcity.equipment.EquipmentEntry;
import io.github.xpakx.habitcity.equipment.EquipmentEntryRepository;
import io.github.xpakx.habitcity.ship.dto.DeployedShip;
import io.github.xpakx.habitcity.ship.dto.ShipRequest;
import io.github.xpakx.habitcity.ship.dto.ShipResponse;
import io.github.xpakx.habitcity.ship.error.NotAShipException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipServiceImpl implements ShipService {
    private final EquipmentEntryRepository entryRepository;
    private final CityRepository cityRepository;
    private final PlayerShipRepository shipRepository;

    @Override
    @Transactional
    public ShipResponse deploy(ShipRequest request, Long cityId, Long userId) {
        EquipmentEntry entry = getEquipmentEntry(request, userId);
        testCityOwnership(cityId, userId);
        entryRepository.delete(entry);
        PlayerShip ship = saveShip(cityId, entry);
        return createShipResponse(ship, cityId, entry);
    }

    private ShipResponse createShipResponse(PlayerShip ship, Long cityId, EquipmentEntry entry) {
        ShipResponse response = new ShipResponse();
        response.setId(ship.getId());
        response.setCode(entry.getShip().getCode());
        response.setName(entry.getShip().getName());
        response.setShipId(entry.getShip().getId());
        response.setCityId(cityId);
        return response;
    }

    private void testCityOwnership(Long cityId, Long userId) {
        if(!cityRepository.existsByIdAndUserId(cityId, userId)) {
            throw new CityNotFoundException();
        }
    }

    private EquipmentEntry getEquipmentEntry(ShipRequest request, Long userId) {
        EquipmentEntry entry = entryRepository.findByIdAndEquipmentUserId(request.getEntryId(), userId).orElseThrow(NotAShipException::new);
        if(entry.getShip() == null) {
            throw new NotAShipException();
        }
        return entry;
    }

    private PlayerShip saveShip(Long cityId, EquipmentEntry entry) {
        PlayerShip ship = new PlayerShip();
        ship.setBlocked(false);
        ship.setCity(cityRepository.getReferenceById(cityId));
        ship.setShip(entry.getShip());
        return shipRepository.save(ship);
    }

    @Override
    public List<DeployedShip> getShipsInCity(Long cityId, Long userId) {
        return shipRepository.findByCityIdAndCityUserId(cityId, userId);
    }
}
