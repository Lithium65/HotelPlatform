package com.example.hotelservice.controller;

import com.example.hotelservice.domain.RoomType;
import com.example.hotelservice.services.HotelService;
import com.example.hotelservice.services.RoomTypeService;
import jakarta.validation.Valid;
import javassist.NotFoundException;
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
import java.util.Map;
import java.util.UUID;

@Controller
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/main/hotels")
public class RoomTypeController {

    @Autowired
    private final RoomTypeService roomTypeService;

    @Autowired
    private final HotelService hotelService;

    @Value("${upload.path}")
    private String uploadPath;

    public RoomTypeController(RoomTypeService roomTypeService, HotelService hotelService) {
        this.roomTypeService = roomTypeService;
        this.hotelService = hotelService;
    }

    @GetMapping("/{id}/room-types")
    public String showHotelRooms(
            @PathVariable(value = "id") Long id,
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {

        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RoomType> roomTypesPage = roomTypeService.getHotelRoomTypes(id, pageable);

        model.addAttribute("hotelName", hotelService.getHotelById(id).get().getHotelName());
        model.addAttribute("roomTypes", roomTypesPage.getContent());
        model.addAttribute("hotelId", id);
        model.addAttribute("currentPage", roomTypesPage.getNumber());
        model.addAttribute("totalPages", roomTypesPage.getTotalPages() > 0 ? roomTypesPage.getTotalPages() : 0);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);

        return "room-types";
    }

    @GetMapping("/{id}/room-types/create")
    public String showCreateForm(@PathVariable(value="id") Long id, Model model) {
        model.addAttribute("roomType", new RoomType());
        model.addAttribute("hotelId", id);
        return "/room-type-form";
    }

    @PostMapping("/{hotelId}/room-types/create")
    public String createRoomType(@PathVariable(value="hotelId") Long hotelId, @RequestParam("file1") MultipartFile file1, @RequestParam("file2") MultipartFile file2, @RequestParam("file3") MultipartFile file3, @ModelAttribute RoomType roomType, BindingResult bindingResult, Map<String, Object> model) throws IOException {
        if (bindingResult.hasErrors()) {
            return "room-type-form";
        }
        if (file1 != null && !file1.isEmpty()) {
            String uuidFile1 = UUID.randomUUID().toString();
            String resultFilename1 = uuidFile1 + '.' + file1.getOriginalFilename();
            File uploadDir1 = new File(uploadPath);

            if (!uploadDir1.exists()) {
                uploadDir1.mkdirs();
            }

            file1.transferTo(new File(uploadDir1, resultFilename1));
            roomType.setFilename1(resultFilename1);
        }

        if (file2 != null && !file2.isEmpty()) {
            String uuidFile2 = UUID.randomUUID().toString();
            String resultFilename2 = uuidFile2 + '.' + file2.getOriginalFilename();
            File uploadDir2 = new File(uploadPath);

            if (!uploadDir2.exists()) {
                uploadDir2.mkdirs();
            }

            file2.transferTo(new File(uploadDir2, resultFilename2));
            roomType.setFilename2(resultFilename2);
        }

        if (file3 != null && !file3.isEmpty()) {
            String uuidFile3 = UUID.randomUUID().toString();
            String resultFilename3 = uuidFile3 + '.' + file3.getOriginalFilename();
            File uploadDir3 = new File(uploadPath);

            if (!uploadDir3.exists()) {
                uploadDir3.mkdirs();
            }

            file3.transferTo(new File(uploadDir3, resultFilename3));
            roomType.setFilename3(resultFilename3);
        }

        else return "redirect:/main/hotels/" + hotelId + "/room-types/create";
        try {
            roomType.setHotel(hotelService.getHotelById(hotelId).orElseThrow(() -> new NotFoundException("Hotel not found")));
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        roomTypeService.createRoomType(roomType);
        return "redirect:/main/hotels/" + hotelId + "/room-types";
    }

    @PostMapping("/{id}/room-types/{roomId}/room-type-delete")
    public String roomTypeDelete(@PathVariable(value="roomId") Long roomId, @PathVariable(value="id") Long id, Model model){
        RoomType roomType = roomTypeService.getRoomTypeById(roomId);
        roomTypeService.deleteRoomType(roomId);
        return ("redirect:/main/hotels/" + id + "/room-types");
    }

    @GetMapping("/{id}/room-types/{roomId}/edit")
    public String editRoomType(@PathVariable(value="roomId") Long roomId, @PathVariable(value="id") Long id, Model model) {
        RoomType roomType = roomTypeService.getRoomTypeById(roomId);
        model.addAttribute("roomType", roomType);
        model.addAttribute("hotelId", id);
        return "room-type-edit";
    }

    @PostMapping("/{id}/room-types/{roomId}/edit")
    public String updateRoomType(
            @PathVariable(value = "roomId") Long roomId,
            @PathVariable(value = "id") Long id,
            @RequestParam("file1") MultipartFile file1,
            @RequestParam("file2") MultipartFile file2,
            @RequestParam("file3") MultipartFile file3,
            @Valid RoomType roomType,
            BindingResult result,
            Map<String, Object> model) throws IOException {



        RoomType existingRoomType = roomTypeService.getRoomTypeById(roomId);

        if (file1 != null && !file1.isEmpty()) {
            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file1.getOriginalFilename();
            file1.transferTo(new File(uploadPath + "/" + resultFilename));
            roomType.setFilename1(resultFilename);
        } else {
            roomType.setFilename1(existingRoomType.getFilename1());
        }

        if (file2 != null && !file2.isEmpty()) {
            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file2.getOriginalFilename();
            file2.transferTo(new File(uploadPath + "/" + resultFilename));
            roomType.setFilename2(resultFilename);
        } else {
            roomType.setFilename2(existingRoomType.getFilename2());
        }

        if (file3 != null && !file3.isEmpty()) {
            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file3.getOriginalFilename();
            file3.transferTo(new File(uploadPath + "/" + resultFilename));
            roomType.setFilename3(resultFilename);
        } else {
            roomType.setFilename3(existingRoomType.getFilename3());
        }
        roomType.setId(roomId);
        try {
            roomType.setHotel(hotelService.getHotelById(id).orElseThrow(() -> new NotFoundException("Hotel not found")));
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        roomTypeService.updateRoomType(roomType);
        return "redirect:/main/hotels/" + id + "/room-types";
    }

}