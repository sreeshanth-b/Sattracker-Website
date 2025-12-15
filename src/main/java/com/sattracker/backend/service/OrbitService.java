package com.sattracker.backend.service;

import com.sattracker.backend.model.GroundStation;
import com.sattracker.backend.model.SatellitePositionResponse;
import com.sattracker.backend.model.Tle;
import org.orekit.frames.*;
import org.orekit.propagation.analytical.tle.*;
import org.orekit.time.*;
import org.orekit.utils.*;
import org.springframework.stereotype.Service;
import org.orekit.bodies.*;

@Service
public class OrbitService {

    public SatellitePositionResponse computePosition(
            Tle tleEntity,
            GroundStation station
    ) {

        // 1) Build TLE
        TLE tle = new TLE(
                tleEntity.getLine1(),
                tleEntity.getLine2()
        );

        // 2) Propagator
        TLEPropagator propagator =
                TLEPropagator.selectExtrapolator(tle);

        // 3) Time
        AbsoluteDate now = new AbsoluteDate(
                new java.util.Date(),
                TimeScalesFactory.getUTC()
        );

        // 4) TEME state
        Frame teme = FramesFactory.getTEME();
        PVCoordinates pvTEME =
                propagator.getPVCoordinates(now, teme);

        // 5) Earth / ITRF
        Frame itrf = FramesFactory.getITRF(
                IERSConventions.IERS_2010, true
        );

        Transform temeToItrf =
                teme.getTransformTo(itrf, now);

        PVCoordinates pvECEF =
                temeToItrf.transformPVCoordinates(pvTEME);

        // 6) Earth model
        OneAxisEllipsoid earth =
                new OneAxisEllipsoid(
                        Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                        Constants.WGS84_EARTH_FLATTENING,
                        itrf
                );

        // Satellite sub-point
        GeodeticPoint satPoint =
                earth.transform(
                        pvECEF.getPosition(),
                        itrf,
                        now
                );

        // 7) Ground station
        GeodeticPoint stationGeo =
                new GeodeticPoint(
                        Math.toRadians(station.getLatitude()),
                        Math.toRadians(station.getLongitude()),
                        station.getAltitude()
                );

        TopocentricFrame stationFrame =
                new TopocentricFrame(
                        earth,
                        stationGeo,
                        station.getName()
                );

        Frame earthFrame = earth.getBodyFrame();

        double azimuth = Math.toDegrees(
                stationFrame.getAzimuth(
                        pvECEF.getPosition(),
                        earthFrame,
                        now
                )
        );

        double elevation = Math.toDegrees(
                stationFrame.getElevation(
                        pvECEF.getPosition(),
                        earthFrame,
                        now
                )
        );

        double rangeKm =
                stationFrame.getRange(
                        pvECEF.getPosition(),
                        earthFrame,
                        now
                ) / 1000.0;

        // 8) Response
        SatellitePositionResponse response =
                new SatellitePositionResponse();

        response.setLatitude(
                Math.toDegrees(satPoint.getLatitude())
        );
        response.setLongitude(
                Math.toDegrees(satPoint.getLongitude())
        );
        response.setAltitude(satPoint.getAltitude());
        response.setVelocity(
                pvECEF.getVelocity().getNorm()
        );

        response.setAzimuth(azimuth);
        response.setElevation(elevation);
        response.setRange(rangeKm);
        response.setTimestamp(now.toString());

        return response;
    }
}
