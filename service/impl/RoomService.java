package com.hma.hotel.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hma.hotel.dto.Response;
import com.hma.hotel.dto.RoomDto;
import com.hma.hotel.entity.Room;
import com.hma.hotel.exception.OurException;
import com.hma.hotel.repo.BookingRepo;
import com.hma.hotel.repo.RoomRepo;
import com.hma.hotel.service.AwsS3Service;
import com.hma.hotel.service.interfac.IRoomService;
import com.hma.hotel.utils.Utils;

@Service
public class RoomService implements IRoomService{


    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private AwsS3Service awsS3Service;



    @Override
    public Response addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description) {
     
        Response response = new Response();

        try {
            String imageUrl = awsS3Service.saveImageToS3(photo);
            Room room = new Room();
            room.setRoomPhotoUrl(imageUrl);
            room.setRoomType(roomType);
            room.setRoomPrice(roomPrice);
            room.setRoomDescription(description);
            Room savedRoom = roomRepo.save(room);
            RoomDto roomDTO = Utils.mapRoomEntityToRoomDTO(savedRoom);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoom(roomDTO);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;

    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepo.findDistinctRoomTypes();
    }

    @Override
    public Response getAllRooms() {
        
        Response response = new Response();

        try {
            List<Room> roomList = roomRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<RoomDto> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoomList(roomDTOList);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteRoom(Long roomId) {
        
        Response response = new Response();

        try {
            roomRepo.findById(roomId).orElseThrow(() -> new OurException("Room Not Found"));
            roomRepo.deleteById(roomId);
            response.setStatusCode(200);
            response.setMessage("successful");

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response updateRoom(Long roomId, String description, String roomType, BigDecimal roomPrice,
            MultipartFile photo) {
                
                Response response = new Response();

                try {
                    String imageUrl = null;
                    if (photo != null && !photo.isEmpty()) {
                        imageUrl = awsS3Service.saveImageToS3(photo);
                    }
                    Room room = roomRepo.findById(roomId).orElseThrow(() -> new OurException("Room Not Found"));
                    if (roomType != null) room.setRoomType(roomType);
                    if (roomPrice != null) room.setRoomPrice(roomPrice);
                    if (description != null) room.setRoomDescription(description);
                    if (imageUrl != null) room.setRoomPhotoUrl(imageUrl);
        
                    Room updatedRoom = roomRepo.save(room);
                    RoomDto roomDTO = Utils.mapRoomEntityToRoomDTO(updatedRoom);
        
                    response.setStatusCode(200);
                    response.setMessage("successful");
                    response.setRoom(roomDTO);
        
                } catch (OurException e) {
                    response.setStatusCode(404);
                    response.setMessage(e.getMessage());
                } catch (Exception e) {
                    response.setStatusCode(500);
                    response.setMessage("Error saving a room " + e.getMessage());
                }
                return response;
    }

    @Override
    public Response getRoomById(Long roomId) {
        
        Response response = new Response();

        try {
            Room room = roomRepo.findById(roomId).orElseThrow(() -> new OurException("Room Not Found"));
            RoomDto roomDTO = Utils.mapRoomEntityToRoomDTOPlusBookings(room);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoom(roomDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAvailableRoomsByDataAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        Response response = new Response();

        try {
            List<Room> availableRooms = roomRepo.findAvailableRoomsByDatesAndTypes(checkInDate, checkOutDate, roomType);
            List<RoomDto> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(availableRooms);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoomList(roomDTOList);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllAvailableRooms() {
        
        Response response = new Response();

        try {
            List<Room> roomList = roomRepo.getAllAvailableRooms();
            List<RoomDto> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoomList(roomDTOList);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

}