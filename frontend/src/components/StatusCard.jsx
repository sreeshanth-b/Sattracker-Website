import { useEffect, useState } from "react";
import { getSatellitePosition } from "../api/orbitApi";

function StatusCard({ satellite, city }) {
  const [data, setData] = useState(null);

  useEffect(() => {
    getSatellitePosition({ satellite, city })
      .then(setData)
      .catch(console.error);
  }, [satellite, city]);

  if (!data) {
    return <p className="text-center">Loading...</p>;
  }

  return (
    <div className="bg-gray-900 p-6 rounded max-w-xl mx-auto">
      <h2 className="text-xl mb-4 text-center">📡 Live Satellite Status</h2>

      <div className="grid grid-cols-2 gap-4 text-sm">
        <div>Latitude</div><div>{data.latitude.toFixed(2)}°</div>
        <div>Longitude</div><div>{data.longitude.toFixed(2)}°</div>
        <div>Altitude</div><div>{(data.altitude / 1000).toFixed(1)} km</div>
        <div>Velocity</div><div>{data.velocity.toFixed(1)} m/s</div>
        <div>Azimuth</div><div>{data.azimuth.toFixed(1)}°</div>
        <div>Elevation</div><div>{data.elevation.toFixed(1)}°</div>
        <div>Range</div><div>{data.range.toFixed(1)} km</div>
      </div>
    </div>
  );
}

export default StatusCard;
