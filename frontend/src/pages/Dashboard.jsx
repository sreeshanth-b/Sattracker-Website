import { useState } from "react";
import SatelliteSelector from "../components/SatelliteSelector";
import GroundStationSelector from "../components/GroundStationSelector";
import StatusCard from "../components/StatusCard";

function Dashboard() {
  const [satellite, setSatellite] = useState("LES-5");
  const [city, setCity] = useState("Hyderabad");

  return (
    <div className="p-6 space-y-6">
      {/* Header */}
      <h1 className="text-3xl font-bold text-center">
        🛰️ SatTracker Dashboard
      </h1>

      {/* Controls */}
      <div className="flex gap-6 justify-center">
        <SatelliteSelector
          satellite={satellite}
          setSatellite={setSatellite}
        />
        <GroundStationSelector
          city={city}
          setCity={setCity}
        />
      </div>

      {/* Live Status */}
      <StatusCard satellite={satellite} city={city} />
    </div>
  );
}

export default Dashboard;
