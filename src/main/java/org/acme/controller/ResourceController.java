package org.acme.controller;

import io.smallrye.common.constraint.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.lang.management.RuntimeMXBean;

import java.lang.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

@Path("/resource")
public class ResourceController {

    @GET
    @Path("/greet")
    public String greet() {
        return "Hello";
    }

    @GET
    @Path("/cores")
    public Response getNumberOfCores() {
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        int availableProcessors = operatingSystemMXBean.getAvailableProcessors();
        return Response.status(Response.Status.ACCEPTED).entity(availableProcessors).build();
    }

    @GET
    @Path("/cpu")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcessCpuLoad(
            @NotNull
            @QueryParam("cpuIndex") Integer cpuIndex) {
        if (cpuIndex == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No CPU index provided").build();
        }

        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        int availableProcessors = operatingSystemMXBean.getAvailableProcessors();
        long prevUpTime = runtimeMXBean.getUptime();
        double prevProcessCpuLoad = operatingSystemMXBean.getProcessCpuLoad();

        double cpuUsage;
        try {
            Thread.sleep(500);
        } catch (Exception ignored) {
        }

        long upTime = runtimeMXBean.getUptime();
        double processCpuLoad = operatingSystemMXBean.getProcessCpuLoad();

        double elapsedCpuLoad = processCpuLoad - prevProcessCpuLoad;
        long elapsedTime = upTime - prevUpTime;
        if (cpuIndex < availableProcessors) {
            cpuUsage = Math.min(99F, elapsedCpuLoad * 100 / elapsedTime);
            return Response.ok("CPU utilization: " + cpuUsage + "%").build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("CPU index out of range").build();
        }
    }


}
