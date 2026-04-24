package com.smartcampus.resource;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final DataStore store = DataStore.getInstance();

    @GET
    public Response getAllSensors(@QueryParam("type") String type) {
        List<Sensor> list = new ArrayList<>(store.getSensors().values());
        if (type != null && !type.isEmpty()) {
            list = list.stream()
                    .filter(s -> s.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }
        return Response.ok(list).build();
    }

@POST
public Response createSensor(Sensor sensor) {

    if (sensor == null) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(java.util.Map.of(
                        "error", "Bad Request",
                        "message", "Request body is missing"
                ))
                .build();
    }

    if (sensor.getId() == null || sensor.getId().isEmpty()) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(java.util.Map.of(
                        "error", "Bad Request",
                        "message", "Sensor ID is required"
                ))
                .build();
    }

    if (store.getSensors().containsKey(sensor.getId())) {
        return Response.status(Response.Status.CONFLICT)
                .entity(java.util.Map.of(
                        "error", "Conflict",
                        "message", "Sensor already exists"
                ))
                .build();
    }

    if (sensor.getType() == null || sensor.getType().isEmpty()) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(java.util.Map.of(
                        "error", "Bad Request",
                        "message", "Sensor type is required"
                ))
                .build();
    }

    if (sensor.getStatus() == null ||
            !(sensor.getStatus().equalsIgnoreCase("ACTIVE") ||
              sensor.getStatus().equalsIgnoreCase("MAINTENANCE") ||
              sensor.getStatus().equalsIgnoreCase("OFFLINE"))) {

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(java.util.Map.of(
                        "error", "Bad Request",
                        "message", "Invalid sensor status"
                ))
                .build();
    }

    if (sensor.getRoomId() == null ||
            !store.getRooms().containsKey(sensor.getRoomId())) {

        throw new LinkedResourceNotFoundException(
                "Room '" + sensor.getRoomId() + "' does not exist.");
    }

    store.getSensors().put(sensor.getId(), sensor);
    store.getRooms().get(sensor.getRoomId())
            .getSensorIds().add(sensor.getId());

    store.getReadings().put(sensor.getId(), new java.util.ArrayList<>());

    return Response.status(Response.Status.CREATED).entity(sensor).build();
}

    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Sensor not found: " + sensorId + "\"}")
                    .build();
        }
        return Response.ok(sensor).build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsResource(
            @PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
    
    @DELETE
    @Path("/{sensorId}")
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {

        Sensor sensor = store.getSensors().get(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(java.util.Map.of(
                            "error", "Not Found",
                            "message", "Sensor not found: " + sensorId
                    ))
                    .build();
        }

        store.getSensors().remove(sensorId);
        store.getReadings().remove(sensorId);

        store.getRooms().get(sensor.getRoomId())
                .getSensorIds().remove(sensorId);

        return Response.noContent().build();
}
}