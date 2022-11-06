package io.github.xpakx.habitcity.ship;

import io.github.xpakx.habitcity.city.CityRepository;
import io.github.xpakx.habitcity.city.error.CityNotFoundException;
import io.github.xpakx.habitcity.city.error.NotEnoughSpaceException;
import io.github.xpakx.habitcity.clients.ExpeditionPublisher;
import io.github.xpakx.habitcity.equipment.*;
import io.github.xpakx.habitcity.equipment.error.EquipmentNotFoundException;
import io.github.xpakx.habitcity.ship.dto.*;
import io.github.xpakx.habitcity.ship.error.NotAShipException;
import io.github.xpakx.habitcity.ship.error.WrongShipChoiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipServiceImpl implements ShipService {
    private final EquipmentEntryRepository entryRepository;
    private final CityRepository cityRepository;
    private final PlayerShipRepository shipRepository;
    private final EquipmentService equipment;
    private final UserEquipmentRepository equipmentRepository;
    private final ExpeditionPublisher publisher;

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

    @Override
    @Transactional
    public ExpeditionResponse sendShips(ExpeditionRequest request, Long cityId, Long userId) {
        testExpeditionRequest(request, cityId, userId);
        List<PlayerShip> shipsToSend = shipRepository.findByCityIdAndIdIn(cityId, request.getShips().stream().map(ExpeditionShip::getShipId).toList());
        testShips(request, shipsToSend);
        List<EquipmentEntry> entries = prepareEquipmentEntries(request, userId);
        entryRepository.saveAll(entries.stream().filter((a -> a.getAmount() > 0)).toList());
        entryRepository.deleteAll(entries.stream().filter((a -> a.getAmount() <= 0)).toList());
        shipRepository.saveAll(shipsToSend);
        publisher.sendExpedition(request, shipsToSend, entries.stream().map(EquipmentEntry::getResource).filter(Objects::nonNull).toList(), userId);
        return null;
    }

    private List<EquipmentEntry> prepareEquipmentEntries(ExpeditionRequest request, Long userId) {
        UserEquipment eq = equipmentRepository.getByUserId(userId).orElseThrow(EquipmentNotFoundException::new);
        List<EquipmentEntry> entries = entryRepository.getByEquipmentId(eq.getId());
        equipment.subtractResources(request, entries);
        return entries;
    }

    private void testShips(ExpeditionRequest request, List<PlayerShip> shipsToSend) {
        if(request.getShips().size() != shipsToSend.size()) {
            throw new WrongShipChoiceException();
        }
        Map<Long, List<ExpeditionEquipment>> equipmentMap = request.getShips().stream()
                .filter((a) -> a.getEquipment() != null)
                .collect(Collectors.toMap(ExpeditionShip::getShipId, ExpeditionShip::getEquipment));
        for(PlayerShip ship : shipsToSend) {
            if(ship.isBlocked()) {
                throw new WrongShipChoiceException("Some ships are already on expedition!");
            }
            testShipCargo(equipmentMap, ship);
            ship.setBlocked(true);
        }
    }

    private void testShipCargo(Map<Long, List<ExpeditionEquipment>> equipmentMap, PlayerShip ship) {
        Integer maxCargo = ship.getShip().getMaxCargo();
        int realCargo = equipmentMap.getOrDefault(ship.getId(), new ArrayList<>()).stream()
                .mapToInt(ExpeditionEquipment::getAmount).sum();
        if(realCargo > maxCargo) {
            throw new NotEnoughSpaceException();
        }
    }

    private void testExpeditionRequest(ExpeditionRequest request, Long cityId, Long userId) {
        testCityOwnership(cityId, userId);
        if(request.getShips() == null || request.getShips().size() < 1) {
            throw new WrongShipChoiceException("You must choose ships for expedition!");
        }
    }
}
