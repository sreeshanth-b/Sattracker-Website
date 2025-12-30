import { useEffect, useRef } from "react";
import {
  getSatellitePosition,
  getOrbitTrack,
} from "../api/orbitApi";

function CesiumGlobe() {
  const containerRef = useRef(null);
  const viewerRef = useRef(null);
  const satelliteRef = useRef(null);
  const orbitPathRef = useRef(null);
  const intervalRef = useRef(null);

  useEffect(() => {
    if (!containerRef.current || !window.Cesium) return;

    const Cesium = window.Cesium;

    // 1️⃣ Create Cesium Viewer (ONCE)
    const viewer = new Cesium.Viewer(containerRef.current, {
      terrainProvider: Cesium.createWorldTerrain(),
      animation: true,
      timeline: false,
      baseLayerPicker: false,
      geocoder: false,
      homeButton: true,
      sceneModePicker: true,
      navigationHelpButton: false,
      fullscreenButton: false,
    });

    viewer.scene.globe.enableLighting = false;
    viewer.scene.skyBox.show = true;
    viewer.scene.skyAtmosphere.show = true;

    viewerRef.current = viewer;

    // 2️⃣ Satellite entity (ONCE)
    satelliteRef.current = viewer.entities.add({
      name: "Satellite",
      point: {
        pixelSize: 10,
        color: Cesium.Color.YELLOW,
      },
    });

    // 3️⃣ Load ORBIT PATH (static polyline)
    const loadOrbitPath = async () => {
      try {
        const track = await getOrbitTrack({
          satellite: "LES-5",
          city: "Hyderabad",
        });

        const positions = track.map((p) =>
          Cesium.Cartesian3.fromDegrees(
            p.longitude,
            p.latitude,
            p.altitude
          )
        );

        orbitPathRef.current = viewer.entities.add({
          name: "Orbit Path",
          polyline: {
            positions,
            width: 2,
            material: Cesium.Color.CYAN,
          },
        });
      } catch (err) {
        console.error("❌ Orbit path failed:", err);
      }
    };

    // 4️⃣ Update satellite position (LIVE)
    const updateSatellite = async () => {
      try {
        const data = await getSatellitePosition({
          satellite: "LES-5",
          city: "Hyderabad",
        });

        const { latitude, longitude, altitude } = data;

        const position = Cesium.Cartesian3.fromDegrees(
          longitude,
          latitude,
          altitude
        );

        satelliteRef.current.position = position;

        // Follow satellite smoothly
        viewer.camera.flyTo({
          destination: Cesium.Cartesian3.fromDegrees(
            longitude,
            latitude,
            altitude + 2_000_000
          ),
          duration: 1.2,
        });
      } catch (err) {
        console.error("❌ Satellite update failed:", err);
      }
    };

    // 5️⃣ Init
    loadOrbitPath();
    updateSatellite();

    // 6️⃣ Update every second
    intervalRef.current = setInterval(updateSatellite, 1000);

    // 7️⃣ Cleanup
    return () => {
      if (intervalRef.current) clearInterval(intervalRef.current);
      if (viewerRef.current) viewerRef.current.destroy();
    };
  }, []);

  return (
    <div
      ref={containerRef}
      style={{
        width: "100%",
        height: "100%",
      }}
    />
  );
}

export default CesiumGlobe;
