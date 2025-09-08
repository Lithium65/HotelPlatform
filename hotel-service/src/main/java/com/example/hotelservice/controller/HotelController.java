package com.example.hotelservice.controller;

import com.example.hotelservice.domain.Hotel;
import com.example.hotelservice.domain.Reservation;
import com.example.hotelservice.domain.Room;
import com.example.hotelservice.domain.RoomType;
import com.example.hotelservice.services.HotelService;
import com.example.hotelservice.services.RoomService;
import com.example.hotelservice.services.RoomTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/main/hotels")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @Autowired
    private RoomTypeService roomTypeService;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping
    public String showHotels(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "hotelName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String country) {

        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Hotel> hotelsPage;

        if(page < 0){
            page = 0;
        }

        if (country != null && !country.isEmpty()) {
            hotelsPage = hotelService.getHotelsByCountry(country, pageable);
        } else {
            hotelsPage = hotelService.getAllHotels(pageable);
        }

        model.addAttribute("hotels", hotelsPage.getContent());
        model.addAttribute("currentPage", hotelsPage.getNumber());
        model.addAttribute("totalPages", hotelsPage.getTotalPages() > 0 ? hotelsPage.getTotalPages() : 0);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("country", country != null ? country : "");

        return "hotels";
    }

    @PostMapping("/{id}/delete")
    public String hotelDelete(@PathVariable(value="id") Long id, Model model){
        Hotel hotel = hotelService.getHotelById(id);
        roomTypeService.deleteHotelRoomTypes(id);

        hotelService.deleteHotel(id);
        return "redirect:/main/hotels";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("hotel", new Hotel());
        return "/hotel-form";
    }

    @PostMapping("/create")
    public String createHotel(@RequestParam("file1") MultipartFile file1, @ModelAttribute Hotel hotel,
                                 BindingResult bindingResult, Map<String, Object> model) throws IOException {
        if (bindingResult.hasErrors()) {
            return "hotel-form";
        }
        if (file1 != null && !file1.isEmpty()) {
            String uuidFile1 = UUID.randomUUID().toString();
            String resultFilename1 = uuidFile1 + '.' + file1.getOriginalFilename();
            File uploadDir1 = new File(uploadPath);

            if (!uploadDir1.exists()) {
                uploadDir1.mkdirs();
            }

            file1.transferTo(new File(uploadDir1, resultFilename1));
            hotel.setFilename1(resultFilename1);
        }

        else return "redirect:/main/hotels/create";
        hotelService.createHotel(hotel);
        return "redirect:/main/hotels";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Hotel hotel = hotelService.getHotelById(id);
        model.addAttribute("hotel.id", hotel.getId());
        model.addAttribute("hotel", hotel);
        return "hotel-edit-form";
    }

    @PostMapping("/{id}/edit")
    public String updateHotel(@PathVariable Long id, @RequestParam("file1") MultipartFile file1, @ModelAttribute Hotel hotel, BindingResult bindingResult, Map<String, Object> model) throws IOException {
        if (bindingResult.hasErrors()) {
            return "editHotel";
        }
        Hotel existingHotel = hotelService.getHotelById(id);
        if (file1 != null && !file1.isEmpty()) {
            String uuidFile1 = UUID.randomUUID().toString();
            String resultFilename1 = uuidFile1 + '.' + file1.getOriginalFilename();
            File uploadDir1 = new File(uploadPath);

            if (!uploadDir1.exists()) {
                uploadDir1.mkdirs();
            }

            file1.transferTo(new File(uploadDir1, resultFilename1));
            if (!uploadDir1.exists()) {
                uploadDir1.mkdirs();
            }

            file1.transferTo(new File(uploadDir1, resultFilename1));
            hotel.setFilename1(resultFilename1);
        } else {
            hotel.setFilename1(existingHotel.getFilename1());
        }
        hotelService.updateHotel(hotel);
        return "redirect:/main/hotels";
    }
}
