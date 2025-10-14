package com.example.hotelservice.services.impl;

import com.example.hotelservice.domain.Hotel;
import com.example.hotelservice.domain.RoomType;
import com.example.hotelservice.services.FileService;
import com.example.hotelservice.services.HotelService;
import com.example.hotelservice.services.RoomTypeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
public class ManagementService {

    @Autowired
    private FileService fileStorageService;

    @Autowired
    private RoomTypeService roomTypeService;

    @Autowired
    private HotelService hotelService;

    public void createRoomTypeWithFiles(Long hotelId, RoomType roomType,
                                        MultipartFile file1, MultipartFile file2,
                                        MultipartFile file3) throws IOException {

        processRoomTypeFiles(roomType, file1, file2, file3);

        if (roomType.getFilename1() == null && roomType.getFilename2() == null && roomType.getFilename3() == null) {
            throw new IllegalArgumentException("At least one file must be uploaded");
        }

        Hotel hotel = hotelService.getHotelById(hotelId)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found"));
        roomType.setHotel(hotel);

        roomTypeService.createRoomType(roomType);
    }

    private void processRoomTypeFiles(RoomType roomType, MultipartFile file1,
                                      MultipartFile file2, MultipartFile file3) throws IOException {
        if (file1 != null && !file1.isEmpty()) {
            roomType.setFilename1(fileStorageService.storeFile(file1));
        }
        if (file2 != null && !file2.isEmpty()) {
            roomType.setFilename2(fileStorageService.storeFile(file2));
        }
        if (file3 != null && !file3.isEmpty()) {
            roomType.setFilename3(fileStorageService.storeFile(file3));
        }
    }

    public void updateRoomTypeWithFiles(Long roomId, Long hotelId, RoomType roomType,
                                        MultipartFile file1, MultipartFile file2,
                                        MultipartFile file3) throws IOException {

        RoomType existingRoomType = roomTypeService.getRoomTypeById(roomId);

        processRoomTypeFilesForUpdate(roomType, existingRoomType, file1, file2, file3);

        roomType.setId(roomId);
        Hotel hotel = hotelService.getHotelById(hotelId)
                .orElseThrow(() -> new EntityNotFoundException("Отель не найден"));
        roomType.setHotel(hotel);

        roomTypeService.updateRoomType(roomType);
    }

    private void processRoomTypeFilesForUpdate(RoomType newRoomType, RoomType existingRoomType,
                                               MultipartFile file1, MultipartFile file2,
                                               MultipartFile file3) throws IOException {

        if (file1 != null && !file1.isEmpty()) {
            newRoomType.setFilename1(fileStorageService.storeFile(file1));
        } else {
            newRoomType.setFilename1(existingRoomType.getFilename1());
        }

        if (file2 != null && !file2.isEmpty()) {
            newRoomType.setFilename2(fileStorageService.storeFile(file2));
        } else {
            newRoomType.setFilename2(existingRoomType.getFilename2());
        }

        if (file3 != null && !file3.isEmpty()) {
            newRoomType.setFilename3(fileStorageService.storeFile(file3));
        } else {
            newRoomType.setFilename3(existingRoomType.getFilename3());
        }
    }
}
