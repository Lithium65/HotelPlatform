package com.example.hotelservice.controller;

import com.example.hotelservice.domain.*;
import com.example.hotelservice.services.*;
import jakarta.servlet.http.HttpSession;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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

@Controller
public class GreetingController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final RoomService roomService;

    @Autowired
    private final RoomTypeService roomTypeService;

    @Autowired
    private final ReservationService reservationService;

    @Autowired
    private final HotelService hotelService;

    public GreetingController(UserService userService, RoomService roomService, RoomTypeService roomTypeService,
                              ReservationService reservationService, HotelService hotelService) {
        this.userService = userService;
        this.roomService = roomService;
        this.roomTypeService = roomTypeService;
        this.reservationService = reservationService;
        this.hotelService = hotelService;
    }

    @GetMapping("/")
    public String greeting() {
//        Set<Role> role = new HashSet<>();
//        role.add(Role.ADMIN);
//        userService.save(new User(0L, "admin", "$2a$08$7ES8xYd44qKQ1YMdGwk.SO2XNvv/ue1Vixs5Z27OpAZocvyrF/eaq", true, null, null, role));
        return "greeting";
    }

    @GetMapping("/room/{id}")
    public String getRoomDetails(@PathVariable Long id, Model model) {
        RoomType roomType = roomTypeService.getRoomTypeById(id);
        try {
            Hotel hotel = hotelService.getHotelById(roomType.getHotel().getId()).orElseThrow(() -> new NotFoundException("Hotel not found"));
            model.addAttribute("hotel", hotel);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        model.addAttribute("roomType", roomType);
        return "room-details";
    }

    @GetMapping("/User-Admin-sort")
    public String redirect(@AuthenticationPrincipal User user) {
        if (user.getRoles().contains(Role.ADMIN)) {
            return "redirect:/admin/reservations";
        } else {
            return "redirect:/personal";
        }
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/reserve")
    public String showBookingPage(@RequestParam Long roomTypeId, Model model) {
        model.addAttribute("rooms", roomService.getAllRooms());
        RoomType roomType = roomTypeService.getRoomTypeById(roomTypeId);
        model.addAttribute("roomType", roomType);
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("reservation", new Reservation());
        return "booking-page";
    }

    @PostMapping("/reserve")
    public String makeReservation(Model model, @RequestParam("lastName") String last_name,
                                  @RequestParam("firstName") String first_name,
                                  @RequestParam("phNumber") String ph_number,
                                  @AuthenticationPrincipal User user,
                                  @RequestParam("roomTypeId") Long roomTypeId,
                                  @RequestParam("country") String country,
                                  @RequestParam("otherCountry") String otherCountry,
                                  @RequestParam("checkIn") LocalDate checkIn,
                                  @RequestParam("checkOut") LocalDate checkOut) {
        RoomType roomType = roomTypeService.getRoomTypeById(roomTypeId);
        if (!otherCountry.isEmpty()){
            country = otherCountry;
        }

        Reservation reservation = new Reservation(ph_number, first_name, last_name, country, user, checkIn, checkOut);
        try {
            reservationService.createReservation(reservation, roomType);
            return "redirect:/personal";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("rooms", roomService.getAllRooms());
            model.addAttribute("roomType", roomTypeService.getAllRoomTypes());
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("reservation", new Reservation());
            return "booking-page";
        }
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/personal")
    public String personal(Model model, @AuthenticationPrincipal User user) {
        List<Reservation> reservations = (List<Reservation>) reservationService.getReservationsByUser(user);
        List<String> hotelName = new ArrayList<>(reservations.size());
        for (Reservation reservation : reservations) {
            hotelName.add(reservation.getRoomType().getHotel().getHotelName());
        }
        model.addAttribute("reservations", reservations);
        model.addAttribute("hotelName", hotelName);
        model.addAttribute("user", user);
        return "personal";
    }

    @GetMapping("/hotels-view")
    public String viewHotels(@RequestParam(name = "country", required = false) String country,
                             @RequestParam(name = "city", required = false) String city,
                             Model model) {
        List<Hotel> hotels = hotelService.findAll();
        model.addAttribute("hotels", hotels);
        return "hotels-view";
    }

    @PostMapping("/search")
    public String search(
            @RequestParam("city") String city,
            @RequestParam("numberOfPeople") int numberOfPeople,
            @RequestParam("checkinDate") LocalDate checkinDate,
            @RequestParam("checkoutDate") LocalDate checkoutDate,
            @RequestParam(defaultValue = "false") boolean baby,
            HttpSession session) {
        List<RoomType> roomTypes = roomTypeService.getAvailableRooms(city, numberOfPeople, baby, checkinDate, checkoutDate);
        session.setAttribute("roomTypes", roomTypes);
        return "redirect:/rooms";
    }

    @GetMapping("/rooms")
    public String rooms(
            HttpSession session,
            Model model,
            @RequestParam(defaultValue = "price,asc") String[] sort)
    {
        List<RoomType> roomTypes = (List<RoomType>) session.getAttribute("roomTypes");

        if (roomTypes == null || roomTypes.isEmpty()) {
            return "rooms";
        }

        String sortField = sort[0];
        String sortDirection = sort[1];

        Comparator<RoomType> comparator = Comparator.comparing(roomType -> {
            if (sortField.equals("price")) {
                return roomType.getPrice();
            }
            throw new IllegalArgumentException("Неподдерживаемое поле сортировки: " + sortField);
        });

        if ("desc".equals(sortDirection)) {
            comparator = comparator.reversed();
        }

        roomTypes.sort(comparator);

        model.addAttribute("roomTypes", roomTypes);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", "asc".equals(sortDirection) ? "desc" : "asc");

        return "rooms";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/all-rooms")
    public String allRooms(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "9") int size,
                           @RequestParam(defaultValue = "name,asc") String sort,
                           Model model) {

        String[] sortParams = sort.split(",");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]));
        Page<RoomType> roomTypesPage = roomTypeService.getAllRoomTypes(pageable);

        model.addAttribute("roomTypes", roomTypesPage.getContent());
        model.addAttribute("page", roomTypesPage);
        model.addAttribute("sort", sort);
        return "all-rooms";
    }
}
