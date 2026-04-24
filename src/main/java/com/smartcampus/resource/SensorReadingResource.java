package com.smartcampus.resource;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;
    private final DataStore store = DataStore.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public Response getReadings() {
        Sensor sensor = store.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Sensor not found: " + sensorId + "\"}")
                    .build();
        }
        List<SensorReading> readingList = store.getReadings().get(sensorId);
        return Response.ok(readingList).build();
    }

    @POST
public Response addReading(SensorReading reading) {

    if (reading == null) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(java.util.Map.of(
                        "error", "Bad Request",
                        "message", "Request body is missing"
                ))
                .build();
    }

    Sensor sensor = store.getSensors().get(sensorId);

    if (sensor == null) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(java.util.Map.of(
                        "error", "Not Found",
                        "message", "Sensor not found: " + sensorId
                ))
                .build();
    }

    if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
        throw new SensorUnavailableException(
                "Sensor " + sensorId + " is under MAINTENANCE");
    }

    if (reading.getId() == null || reading.getId().isEmpty()) {
        reading.setId(java.util.UUID.randomUUID().toString());
    }

    if (reading.getTimestamp() == 0) {
        reading.setTimestamp(System.currentTimeMillis());
    }

    store.getReadings().get(sensorId).add(reading);
    sensor.setCurrentValue(reading.getValue());

    return Response.status(Response.Status.CREATED).entity(reading).build();
}
}