package io.github.xpakx.habitcity.ship;

import io.github.xpakx.habitcity.city.City;
import io.github.xpakx.habitcity.city.CityRepository;
import io.github.xpakx.habitcity.city.error.CityNotFoundException;
import io.github.xpakx.habitcity.equipment.EquipmentEntry;
import io.github.xpakx.habitcity.equipment.EquipmentEntryRepository;
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
        saveShip(cityId, entry);
        return new ShipResponse();
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

    private void saveShip(Long cityId, EquipmentEntry entry) {
        PlayerShip ship = new PlayerShip();
        ship.setBlocked(false);
        ship.setCity(cityRepository.getReferenceById(cityId));
        ship.setShip(entry.getShip());
        shipRepository.save(ship);
    }

    @Override
    public List<Ship> getShipsInCity(Long cityId, Long userId) {
        return shipRepository.findByCityIdAndCityUserId(cityId, userId);
    }
}
