function SatelliteSelector({ satellite, setSatellite }) {
  return (
    <div>
      <label className="block text-sm mb-1">Satellite</label>
      <select
        value={satellite}
        onChange={(e) => setSatellite(e.target.value)}
        className="bg-gray-800 text-white p-2 rounded"
      >
        <option value="LES-5">LES-5</option>
        <option value="ISS">ISS</option>
        <option value="CALSPHERE 4A">CALSPHERE 4A</option>
      </select>
    </div>
  );
}

export default SatelliteSelector;
