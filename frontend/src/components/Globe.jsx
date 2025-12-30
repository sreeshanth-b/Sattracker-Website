import { useEffect, useRef } from "react";
import * as Cesium from "cesium";
import "cesium/Build/Cesium/Widgets/widgets.css";

function Globe() {
  const viewerRef = useRef(null);

  useEffect(() => {
    if (viewerRef.current) return;

    viewerRef.current = new Cesium.Viewer("cesiumContainer", {
      animation: false,
      timeline: false,
      baseLayerPicker: false,
      sceneModePicker: false,
      geocoder: false,
      homeButton: false,
      fullscreenButton: false,
      navigationHelpButton: false,
    });

    viewerRef.current.camera.flyHome(0);
  }, []);

  return <div id="cesiumContainer" className="absolute inset-0" />;
}

export default Globe;
