package com.example.hotelservice.controller;

import com.example.hotelservice.domain.*;
import com.example.hotelservice.services.HotelService;
import com.example.hotelservice.services.ReservationService;
import com.example.hotelservice.services.RoomService;
import com.example.hotelservice.services.RoomTypeService;
import com.example.hotelservice.services.impl.ManagementService;
import jakarta.persistence.EntityNotFoundException;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@PreAuthorize("hasAuthority('MANAGER')")
@RequestMapping("/managing-hotel")
public class ManagerController {

    @Autowired
    HotelService hotelService;

    @Autowired
    ReservationService reservationService;

    @Autowired
    RoomTypeService roomTypeService;

    @Autowired
    ManagementService managementService;

    @Autowired
    RoomService roomService;

    @GetMapping()
    public String showManagedHotel(@AuthenticationPrincipal User user, Model model) {
        try {
            Hotel hotel = hotelService.getHotelById(user.getManagedHotel().getId()).orElseThrow(() -> new NotFoundException("Hotel not found"));
            model.addAttribute("hotelName", hotel.getHotelName());
            model.addAttribute("hotelCity", hotel.getCity());
            model.addAttribute("hotelCountry", hotel.getCountry());
            model.addAttribute("hotelStreet", hotel.getStreet());
            model.addAttribute("hotelMapLink", hotel.getGoogleMap());
            model.addAttribute("hotelDescription", hotel.getDescription());
            model.addAttribute("hotelImage", hotel.getFilename1());
            model.addAttribute("managers", hotel.getManagers().size());
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        return "manager/hotel-board";
    }

    @GetMapping("/reservations")
    public String showManagedReservations(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @AuthenticationPrincipal User user, Model model) {
        Page<Reservation> reservationPage;
        Pageable pageable = PageRequest.of(page, size);
        Long hotelId = getManagedHotelIdWithValidation(user);
        try {
            String hotelName = hotelService.getHotelById(hotelId).orElseThrow(() -> new NotFoundException("")).getHotelName();
            List<RoomType> roomTypes = roomTypeService.getHotelRoomTypes(hotelId);
            reservationPage = reservationService.getReservationsByHotelName(hotelService.getHotelById(user.getManagedHotel().getId()).orElseThrow(() -> new NotFoundException("Hotel not found")).getHotelName(), pageable);

            model.addAttribute("reservations", reservationPage.getContent());
            model.addAttribute("page", reservationPage);
            model.addAttribute("hotelName", hotelName != null ? hotelName : "");
            model.addAttribute("roomType", roomTypes);
            return "manager/hotel-reservations";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/reservations")
    public String searchManagerReservations(
            @AuthenticationPrincipal User user,
            Model model,
            @RequestParam(required = false, value = "filter") Long filter,
            @RequestParam(required = false, value = "date") LocalDate filterDate,
            @RequestParam(required = false, value = "checkOut", defaultValue = "false") Boolean isCheckOut) {
        Long hotelId = getManagedHotelIdWithValidation(user);
        List<RoomType> roomTypes = roomTypeService.getHotelRoomTypes(hotelId);
        List<Reservation> reservations;
        try {
            String hotelName = hotelService.getHotelById(hotelId).orElseThrow(() -> new NotFoundException("")).getHotelName();

            if (filterDate != null && filter != null) {
                reservations = reservationService.findFilteredReservationsByRoomTypeAndDate(filter, filterDate, isCheckOut);
            } else if (filterDate != null) {
                reservations = reservationService.findFilteredReservationsByDate(hotelId, filterDate, isCheckOut);
            } else if (filter != null) {
                reservations = reservationService.findFilteredReservationsByRoomType(filter);
            } else reservations = new ArrayList<>();

            model.addAttribute("filter", filter);
            model.addAttribute("date", filterDate);
            model.addAttribute("checkOut", isCheckOut);
            model.addAttribute("reservations", reservations);
            model.addAttribute("roomType", roomTypes);
            model.addAttribute("hotelName", hotelName != null ? hotelName : "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "manager/hotel-reservations";
    }

    @PostMapping("/reservations/{id}/reservation-delete")
    public String reservationDelete(@PathVariable(value = "id") Long id, @AuthenticationPrincipal User user) {
        Long hotelId = getManagedHotelIdWithValidation(user);
        reservationService.deleteReservationWithConformation(id, hotelId);
        return ("redirect:/managing-hotel/reservations");
    }

    @GetMapping("/hotel-rooms")
    public String showRoomTypes(@AuthenticationPrincipal User user,
                                Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "6") int size,
                                @RequestParam(defaultValue = "name") String sortBy,
                                @RequestParam(defaultValue = "asc") String sortOrder) {
        Long hotelId = getManagedHotelIdWithValidation(user);
        roomTypeService.getHotelRoomsModel(hotelId, model, page, size, sortBy, sortOrder);
        List<RoomType> roomTypes = (List<RoomType>) model.getAttribute("roomTypes");

        if (roomTypes != null && !roomTypes.isEmpty()) {
            double avgPrice = roomTypes.stream()
                    .mapToDouble(RoomType::getPrice)
                    .average()
                    .orElse(0);

            long withBaby = roomTypes.stream()
                    .filter(RoomType::isBaby)
                    .count();

            int maxCapacity = roomTypes.stream()
                    .mapToInt(RoomType::getPlaces)
                    .max()
                    .orElse(0);

            model.addAttribute("avgPrice", avgPrice);
            model.addAttribute("withBaby", withBaby);
            model.addAttribute("maxCapacity", maxCapacity);
        } else {
            model.addAttribute("avgPrice", 0);
            model.addAttribute("withBaby", 0);
            model.addAttribute("maxCapacity", 0);
        }
        return "manager/hotel-room-types";
    }

    @GetMapping("/hotel-rooms/create")
    public String createRoomType(@AuthenticationPrincipal User user, Model model) {
        Long hotelId = getManagedHotelIdWithValidation(user);
        model.addAttribute("roomType", new RoomType());
        model.addAttribute("hotelId", hotelId);
        return "manager/room-type-form";
    }

    @PostMapping("/hotel-rooms/create")
    public String createRoomType(@AuthenticationPrincipal User user,
                                 @RequestParam("file1") MultipartFile file1,
                                 @RequestParam("file2") MultipartFile file2,
                                 @RequestParam("file3") MultipartFile file3,
                                 @ModelAttribute RoomType roomType,
                                 BindingResult bindingResult) throws IOException {
        Long hotelId = getManagedHotelIdWithValidation(user);
        if (bindingResult.hasErrors()) {
            return "redirect:/managing-hotel/hotel-rooms/create";
        }
        try {
            managementService.createRoomTypeWithFiles(hotelId, roomType, file1, file2, file3);
            return "redirect:/managing-hotel/hotel-rooms/";

        } catch (EntityNotFoundException e) {
            return "redirect:/managing-hotel/hotel-rooms/create?error=hotel_not_found";
        } catch (IllegalArgumentException e) {
            return "redirect:/managing-hotel/hotel-rooms/create?error=no_files";
        }
    }

    @GetMapping("hotel-rooms/rooms-management")
    public String manageRooms(@AuthenticationPrincipal User user,
                              @RequestParam(required = false, defaultValue = "") RoomType filter,
                              Model model) {
        Long hotelId = getManagedHotelIdWithValidation(user);
        Iterable<Room> rooms;
        List<RoomType> roomType = roomTypeService.getHotelRoomTypes(hotelId);

        if (filter != null) {
            rooms = roomService.findByRoomType(filter);
        } else {
            rooms = roomService.getAllHotelRooms(hotelId);
        }

        model.addAttribute("roomType", roomType);
        model.addAttribute("rooms", rooms);
        model.addAttribute("filter", filter);
        return "manager/rooms-management";
    }

    @PostMapping("hotel-rooms/rooms-management")
    public String manageRooms(@AuthenticationPrincipal User user,
                              @RequestParam int number,
                              @RequestParam(required = false, value = "roomTypeId") Long roomTypeId,
                              Model model) {
        Long hotelId = getManagedHotelIdWithValidation(user);
        if (roomTypeId == null) {
            return "redirect:/managing-hotel/hotel-rooms/rooms-management";
        }

        RoomType roomType = roomTypeService.getRoomTypeById(roomTypeId);
        if (!hotelId.equals(roomType.getHotel().getId())) {
            model.addAttribute("errorMessage", "Неверный айди отеля менеджера!");
            return "redirect:/managing-hotel/hotel-rooms/rooms-management";
        }
        Room room = new Room(number, roomType);

        if (roomService.checkRoomExistence(room)) {
            model.addAttribute("errorMessage", "Комната с таким номером уже существует!");
            return "redirect:/managing-hotel/hotel-rooms/rooms-management";
        }

        roomService.save(room);
        return "redirect:/managing-hotel/hotel-rooms/rooms-management";
    }

    @PostMapping("/hotel-rooms/{id}/room-delete")
    public String deleteRooms(@AuthenticationPrincipal User user, @PathVariable Long id) {
        Long hotelId = getManagedHotelIdWithValidation(user);
        try {
            Optional<Room> room = roomService.findById(id);
            if (!Objects.equals(room.get().getRoomType().getHotel().getId(), hotelId))
                throw new AccessDeniedException("Room is not belonging to this manager");
            List<Reservation> reservation = reservationService.findByRoom(room.get());
            if (reservation != null && !reservation.isEmpty()) {
                return "redirect:/managing-hotel/hotel-rooms/rooms-management";
            }
            roomService.delete(room.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "redirect:/managing-hotel/hotel-rooms/rooms-management";
    }

    private static Long getManagedHotelIdWithValidation(User user) {
        Long hotelId = user.getManagedHotel().getId();
        if (hotelId == null) throw new IllegalArgumentException("Managed hotel is null");
        return hotelId;
    }

}
