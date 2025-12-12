package com.sattracker.backend.service;

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

    public SatellitePositionResponse computePosition(Tle tleEntity) {

        // 1) Build TLE object
        TLE tle = new TLE(
                tleEntity.getLine1(),
                tleEntity.getLine2()
        );

        // 2) Create TLE Propagator
        TLEPropagator propagator = TLEPropagator.selectExtrapolator(tle);

        // 3) Current time
        AbsoluteDate now = new AbsoluteDate();

        // 4) Propagate TEME position
        PVCoordinates pvTEME = propagator.getPVCoordinates(now, FramesFactory.getTEME());

        // 5) Convert TEME → ECEF (ITRF)
        Frame itrf = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
        PVCoordinates pvECEF = FramesFactory.getTEME().getTransformTo(itrf, now).transformPVCoordinates(pvTEME);

        // 6) Convert ECEF to LLA
        OneAxisEllipsoid earth = new OneAxisEllipsoid(
                Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                Constants.WGS84_EARTH_FLATTENING,
                itrf
        );

        GeodeticPoint point = earth.transform(pvECEF.getPosition(), itrf, now);

        // 7) Prepare JSON Response
        SatellitePositionResponse response = new SatellitePositionResponse();
        response.setLatitude(Math.toDegrees(point.getLatitude()));
        response.setLongitude(Math.toDegrees(point.getLongitude()));
        response.setAltitude(point.getAltitude());
        response.setVelocity(pvECEF.getVelocity().getNorm());
        response.setTimestamp(now.toString());

        return response;
    }
}
