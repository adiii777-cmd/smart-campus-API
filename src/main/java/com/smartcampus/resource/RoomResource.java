package com.smartcampus.resource;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final DataStore store = DataStore.getInstance();

    @GET
    public Response getAllRooms() {
        List<Room> roomList = new ArrayList<>(store.getRooms().values());
        return Response.ok(roomList).build();
    }

@POST
public Response createRoom(Room room) {

    if (room == null) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(java.util.Map.of(
                        "error", "Bad Request",
                        "message", "Request body is missing"
                ))
                .build();
    }

    if (room.getId() == null || room.getId().isEmpty()) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(java.util.Map.of(
                        "error", "Bad Request",
                        "message", "Room ID is required"
                ))
                .build();
    }

    if (room.getName() == null || room.getName().isEmpty()) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(java.util.Map.of(
                        "error", "Bad Request",
                        "message", "Room name is required"
                ))
                .build();
    }

    if (room.getCapacity() <= 0) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(java.util.Map.of(
                        "error", "Bad Request",
                        "message", "Capacity must be greater than 0"
                ))
                .build();
    }

    if (store.getRooms().containsKey(room.getId())) {
        return Response.status(Response.Status.CONFLICT)
                .entity(java.util.Map.of(
                        "error", "Conflict",
                        "message", "Room already exists"
                ))
                .build();
    }

    store.getRooms().put(room.getId(), room);

    return Response.status(Response.Status.CREATED).entity(room).build();
}

    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = store.getRooms().get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Room not found: " + roomId + "\"}")
                    .build();
        }
        return Response.ok(room).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRooms().get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Room not found: " + roomId + "\"}")
                    .build();
        }
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Room " + roomId +
                " cannot be deleted because it still has sensors assigned.");
        }
        store.getRooms().remove(roomId);
        return Response.noContent().build();
    }
}