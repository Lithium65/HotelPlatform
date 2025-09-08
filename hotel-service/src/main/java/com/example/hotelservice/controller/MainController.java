package com.example.hotelservice.controller;

import com.example.hotelservice.domain.*;
import com.example.hotelservice.repos.HotelRepo;
import com.example.hotelservice.repos.RoomRepo;
import com.example.hotelservice.services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class MainController {

    @Autowired
    private final RoomService roomService;

    @Autowired
    private final ReservationService reservationService;

    @Autowired
    private final RoomTypeService roomTypeService;

    public MainController(RoomService roomService, ReservationService reservationService, RoomTypeService roomTypeService) {
        this.roomService = roomService;
        this.reservationService = reservationService;
        this.roomTypeService = roomTypeService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/main/hotels/{id}/room-types/add-rooms")
    public String main(@PathVariable(value = "id") Long id,
                       @RequestParam(required = false, defaultValue = "") RoomType filter,
                       Model model) {

        Iterable<Room> rooms;
        List<RoomType> roomType = roomTypeService.getHotelRoomTypes(id);

        if (filter != null) {
            rooms = roomService.findByRoomType(filter);
        } else {
            rooms = roomService.getAllHotelRooms(id);
        }

        model.addAttribute("roomType", roomType);
        model.addAttribute("rooms", rooms);
        model.addAttribute("filter", filter);
        return "main";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/main/hotels/{id}/room-types/add-rooms")
    public String add(
            @AuthenticationPrincipal User user,
            @RequestParam int number,
            @RequestParam("roomTypeId") Long roomTypeId,
            @PathVariable(value = "id") Long id,
            Model model) {

        if(roomTypeId == null) {
            return "redirect:/main/hotels/" + id + "/room-types/add-rooms";
        }

        RoomType roomType = roomTypeService.getRoomTypeById(roomTypeId);
        Room room = new Room(number, roomType);
        Boolean room_check = roomService.checkRoomExistence(room);

        if(room_check) {
            model.addAttribute("errorMessage", "Комната с таким номером уже существует!");
            return main(id, null, model); // Вызываем метод main для заполнения модели данными
        }

        roomService.save(room);
        return "redirect:/main/hotels/" + id + "/room-types/add-rooms";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/main/hotels/{hotelId}/room-types/{roomId}/room-delete")
    public String roomDelete(@PathVariable(value = "hotelId") Long hotelId, @PathVariable(value = "roomId") Long id, Model model) {

        Room room = roomService.findById(id).orElseThrow();
        List<Reservation> reservation = reservationService.findByRoom(roomService.findById(id));

        if(reservation != null && !reservation.isEmpty())
        {
            return ("redirect:/main/" + hotelId + "/room-types");
        }

        roomService.delete(room);
        return ("redirect:/main/hotels/" + hotelId + "/room-types/add-rooms");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/main/reservations")
    public String getReservations(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(required = false) String hotelName,
                                  Model model) {
        Page<Reservation> reservationPage;
        Pageable pageable = PageRequest.of(page, size);

        if (hotelName != null && !hotelName.isEmpty()) {
            reservationPage = reservationService.getReservationsByHotelName(hotelName, pageable);
        } else {
            reservationPage = reservationService.getAllReservations(pageable);
        }

        model.addAttribute("reservations", reservationPage.getContent());
        model.addAttribute("page", reservationPage);
        model.addAttribute("hotelName", hotelName != null ? hotelName : "");
        //model.addAttribute("hotelName", hotelName);
        return "reservations";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/main/reservations")
    public String search(Model model,@RequestParam(required = false, defaultValue = "")
    Long filter) {

        List<RoomType> roomType = roomTypeService.getAllRoomTypes();
        List<Reservation> reservations;

        if (filter != null) {
            RoomType typeFilter = roomTypeService.getRoomTypeById(filter);
            reservations = reservationService.findByRoomType(typeFilter);
        } else {
            reservations = reservationService.getAllReservations();
        }

        model.addAttribute("filter", filter);
        model.addAttribute("reservations", reservations);
        model.addAttribute("roomType", roomType);
        return "reservations";
    }

    @PostMapping("/main/{id}/reservation-delete")
    public String reservationDelete(@PathVariable(value = "id") Long id, @AuthenticationPrincipal User user, Model model) {
        if(user.getRoles().contains(Role.ADMIN) ||
                Objects.equals(reservationService.getReservationById(id).get().getUser().getId(), user.getId()))
            reservationService.deleteReservation(id);

        if(user.getRoles().contains(Role.USER))
            return ("redirect:/personal");
        else return ("redirect:/main/reservations");
    }

}