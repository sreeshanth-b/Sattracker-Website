import { useEffect, useRef } from "react";
import * as Cesium from "cesium";

window.CESIUM_BASE_URL = "/cesium";

function GlobeView() {
  const viewerRef = useRef(null);

  useEffect(() => {
    if (viewerRef.current) return;

    viewerRef.current = new Cesium.Viewer("cesiumContainer", {
      animation: false,
      timeline: false,
      baseLayerPicker: false,
      geocoder: false,
      homeButton: false,
      sceneModePicker: false,
      navigationHelpButton: false,
    });

    viewerRef.current.scene.globe.enableLighting = true;
  }, []);

  return (
    <div
      id="cesiumContainer"
      style={{ width: "100vw", height: "100vh" }}
    />
  );
}

export default GlobeView;
