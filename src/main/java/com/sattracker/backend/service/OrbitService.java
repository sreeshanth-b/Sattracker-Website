package com.sattracker.backend.service;

import com.sattracker.backend.model.*;
import org.orekit.bodies.*;
import org.orekit.frames.*;
import org.orekit.propagation.analytical.tle.*;
import org.orekit.time.*;
import org.orekit.utils.*;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrbitService {

  
    public SatellitePositionResponse computePosition(
            Tle tleEntity,
            GroundStation station,
            String utc
    ) {

        AbsoluteDate date =
                (utc != null && !utc.isBlank())
                        ? new AbsoluteDate(utc, TimeScalesFactory.getUTC())
                        : new AbsoluteDate(new Date(), TimeScalesFactory.getUTC());

        TLE tle = new TLE(tleEntity.getLine1(), tleEntity.getLine2());
        TLEPropagator propagator =
                TLEPropagator.selectExtrapolator(tle);

        Frame teme = FramesFactory.getTEME();
        Frame itrf = FramesFactory.getITRF(
                IERSConventions.IERS_2010,
                true
);


        PVCoordinates pvTEME =
                propagator.getPVCoordinates(date, teme);

        PVCoordinates pvECEF =
                teme.getTransformTo(itrf, date)
                        .transformPVCoordinates(pvTEME);

        OneAxisEllipsoid earth =
                new OneAxisEllipsoid(
                        Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                        Constants.WGS84_EARTH_FLATTENING,
                        itrf
                );

        GeodeticPoint satPoint =
                earth.transform(pvECEF.getPosition(), itrf, date);

        GeodeticPoint stationGeo =
                new GeodeticPoint(
                        Math.toRadians(station.getLatitude()),
                        Math.toRadians(station.getLongitude()),
                        station.getAltitude()
                );

        TopocentricFrame stationFrame =
                new TopocentricFrame(earth, stationGeo, station.getName());

        double az =
                Math.toDegrees(stationFrame.getAzimuth(
                        pvECEF.getPosition(), itrf, date));

        double el =
                Math.toDegrees(stationFrame.getElevation(
                        pvECEF.getPosition(), itrf, date));

        SatellitePositionResponse res =
                new SatellitePositionResponse();

        res.setLatitude(Math.toDegrees(satPoint.getLatitude()));
        res.setLongitude(Math.toDegrees(satPoint.getLongitude()));
        res.setAltitude(satPoint.getAltitude());
        res.setVelocity(pvECEF.getVelocity().getNorm());
        res.setAzimuth(az);
        res.setElevation(el);
        res.setTimestamp(date.toString());

        return res;
    }

    public SatellitePassResponse computeNextPass(
            Tle tleEntity,
            GroundStation station
    ) {

        TLE tle = new TLE(tleEntity.getLine1(), tleEntity.getLine2());
        TLEPropagator propagator =
                TLEPropagator.selectExtrapolator(tle);

        Frame teme = FramesFactory.getTEME();
        Frame itrf = FramesFactory.getITRF(
         IERSConventions.IERS_2010,
                true
);


        OneAxisEllipsoid earth =
                new OneAxisEllipsoid(
                        Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                        Constants.WGS84_EARTH_FLATTENING,
                        itrf
                );

        GeodeticPoint stationGeo =
                new GeodeticPoint(
                        Math.toRadians(station.getLatitude()),
                        Math.toRadians(station.getLongitude()),
                        station.getAltitude()
                );

        TopocentricFrame stationFrame =
                new TopocentricFrame(earth, stationGeo, station.getName());

        AbsoluteDate start =
                new AbsoluteDate(new Date(), TimeScalesFactory.getUTC());
        AbsoluteDate end = start.shiftedBy(24 * 3600);

        double prevEl = -90;
        AbsoluteDate aos = null;
        AbsoluteDate los = null;
        double maxEl = 0;

        for (AbsoluteDate t = start;
             t.compareTo(end) < 0;
             t = t.shiftedBy(10)) {

            PVCoordinates pv =
                    propagator.getPVCoordinates(t, teme);

            PVCoordinates pvEcef =
                    teme.getTransformTo(itrf, t)
                            .transformPVCoordinates(pv);

            double el =
                    Math.toDegrees(
                            stationFrame.getElevation(
                                    pvEcef.getPosition(), itrf, t));

            if (prevEl < 0 && el > 0 && aos == null) aos = t;
            if (prevEl > 0 && el < 0 && aos != null) {
                los = t;
                break;
            }

            maxEl = Math.max(maxEl, el);
            prevEl = el;
        }

        if (aos == null || los == null)
            throw new RuntimeException("No pass in next 24h");

        SatellitePassResponse res =
                new SatellitePassResponse();

        res.setAos(aos.toString());
        res.setLos(los.toString());
        res.setMaxElevation(maxEl);
        res.setDurationSec((long) los.durationFrom(aos));

        return res;
    }


    public RadarResponse computeLiveRadar(
            Tle tleEntity,
            GroundStation station
    ) {

        AbsoluteDate now =
                new AbsoluteDate(new Date(), TimeScalesFactory.getUTC());

        TLE tle = new TLE(tleEntity.getLine1(), tleEntity.getLine2());
        TLEPropagator propagator =
                TLEPropagator.selectExtrapolator(tle);

        Frame teme = FramesFactory.getTEME();
        Frame itrf = FramesFactory.getITRF(
                IERSConventions.IERS_2010,
                true
);


        PVCoordinates pv =
                propagator.getPVCoordinates(now, teme);

        PVCoordinates pvEcef =
                teme.getTransformTo(itrf, now)
                        .transformPVCoordinates(pv);

        OneAxisEllipsoid earth =
                new OneAxisEllipsoid(
                        Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                        Constants.WGS84_EARTH_FLATTENING,
                        itrf
                );

        GeodeticPoint stationGeo =
                new GeodeticPoint(
                        Math.toRadians(station.getLatitude()),
                        Math.toRadians(station.getLongitude()),
                        station.getAltitude()
                );

        TopocentricFrame stationFrame =
                new TopocentricFrame(earth, stationGeo, station.getName());

        double az =
                Math.toDegrees(stationFrame.getAzimuth(
                        pvEcef.getPosition(), itrf, now));

        double el =
                Math.toDegrees(stationFrame.getElevation(
                        pvEcef.getPosition(), itrf, now));

        RadarResponse res = new RadarResponse();
        res.setAzimuth(az);
        res.setElevation(el);
        res.setVisible(el > 0);
        res.setTimestamp(now.toString());

        return res;
    }
}
