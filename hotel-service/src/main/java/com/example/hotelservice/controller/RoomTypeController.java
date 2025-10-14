package com.example.hotelservice.controller;

import com.example.hotelservice.domain.RoomType;
import com.example.hotelservice.services.HotelService;
import com.example.hotelservice.services.RoomTypeService;
import com.example.hotelservice.services.impl.ManagementService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/admin/hotels")
public class RoomTypeController {

    @Autowired
    private final RoomTypeService roomTypeService;

    @Autowired
    private final HotelService hotelService;

    @Autowired
    private final ManagementService managementService;

    public RoomTypeController(RoomTypeService roomTypeService, HotelService hotelService,
                              ManagementService managementService) {
        this.roomTypeService = roomTypeService;
        this.hotelService = hotelService;
        this.managementService = managementService;
    }

    @GetMapping("/{id}/room-types")
    public String showHotelRooms(
            @PathVariable(value = "id") Long id,
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        roomTypeService.getHotelRoomsModel(id, model, page, size, sortBy, sortOrder);
        return "room-types";
    }

    @GetMapping("/{id}/room-types/create")
    public String showCreateForm(@PathVariable(value = "id") Long id, Model model) {
        model.addAttribute("roomType", new RoomType());
        model.addAttribute("hotelId", id);
        return "/room-type-form";
    }

    @PostMapping("/{hotelId}/room-types/create")
    public String createRoomType(
            @PathVariable(value = "hotelId") Long hotelId,
            @RequestParam("file1") MultipartFile file1,
            @RequestParam("file2") MultipartFile file2,
            @RequestParam("file3") MultipartFile file3,
            @ModelAttribute RoomType roomType,
            BindingResult bindingResult) throws IOException {

        if (bindingResult.hasErrors()) {
            return "room-type-form";
        }

        try {
            managementService.createRoomTypeWithFiles(hotelId, roomType, file1, file2, file3);
            return "redirect:/admin/hotels/" + hotelId + "/room-types";

        } catch (EntityNotFoundException e) {
            return "redirect:/admin/hotels/" + hotelId + "/room-types/create?error=hotel_not_found";
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/hotels/" + hotelId + "/room-types/create?error=no_files";
        }
    }

    @PostMapping("/{id}/room-types/{roomId}/room-type-delete")
    public String roomTypeDelete(
            @PathVariable(value = "roomId") Long roomId,
            @PathVariable(value = "id") Long id) {

        roomTypeService.deleteRoomType(roomId);
        return "redirect:/admin/hotels/" + id + "/room-types";
    }

    @GetMapping("/{id}/room-types/{roomId}/edit")
    public String editRoomType(
            @PathVariable(value = "roomId") Long roomId,
            @PathVariable(value = "id") Long id,
            Model model) {

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
            @Valid RoomType roomType) throws IOException {

        managementService.updateRoomTypeWithFiles(roomId, id, roomType, file1, file2, file3);
        return "redirect:/admin/hotels/" + id + "/room-types";
    }
}