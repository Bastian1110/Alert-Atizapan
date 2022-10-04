package mx.itesm.incidentesatizapan

import android.graphics.Color
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import mx.itesm.incidentesatizapan.databinding.FragmentMapsBinding

/**
 * @author: David Damian
 * The view for the maps fragment, which will be used to display local incidents.
 */
class MapsFragment : Fragment(), GoogleMap.OnMarkerClickListener {
    private val viewModel: MapsViewModel by viewModels()
    private lateinit var binding: FragmentMapsBinding
    private lateinit var googleMap: GoogleMap

    private val callback = OnMapReadyCallback { map ->
        /**
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val atizapan = LatLng(19.589693, -99.229509)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(atizapan, 13F))
        map.setOnMarkerClickListener(this)
        googleMap = map

        viewModel.getIncidents()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservables()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun setObservables() {
        viewModel.incidents.observe(viewLifecycleOwner) { incidents ->
            addMarkers(incidents)
        }
    }

    /**
     * Add markers to the map in the incident locations. Uses a different icon for each type of
     * incident.
     * @param Incidents, the incidents to represent.
     */
    private fun addMarkers(incidents: Incidents) {
        for (incident: Incident in incidents.incidentList) {
            // Ignore incidents without coordinates
            if (!incident.hasCoordinate()
                || !incident.coordinate.hasLatitude()
                || !incident.coordinate.hasLongitude()) {
                continue
            }

            val title = viewModel.parseIncidentTitle(incident)
            val icon = getIncidentTypeIcon(incident.incidentType)

            // Draw a marker in the location of the incident
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(incident.coordinate.latitude, incident.coordinate.longitude))
                    .title(title)
                    .icon(BitmapDescriptorFactory.fromResource(icon)))

            if (marker != null) {
                marker.tag = incident
            }

            // Draw a circle indicating the risk area that people should avoid
            // TODO: Configure a different color for each type of incident
            if (incident.riskRadius >= 0) {
                googleMap.addCircle(
                    CircleOptions()
                        .center(LatLng(incident.coordinate.latitude, incident.coordinate.longitude))
                        .radius(incident.riskRadius)
                        .strokeColor(Color.RED)
                        .fillColor(Color.argb(50, 255, 0, 0)))
            }
        }
    }

    /**
     * Get the icon for each type of incident.
     * @param Incident.IncidentType, the type of incident
     * @return Int, a reference to the incident type icon.
     */
    private fun getIncidentTypeIcon(type: Incident.IncidentType?): Int {
        return when (type) {
            Incident.IncidentType.FIRE -> R.drawable.flames
            Incident.IncidentType.FLOODING -> R.drawable.flood
            Incident.IncidentType.CAR_ACCIDENT -> R.drawable.car_collision
            Incident.IncidentType.GAS_LEAK -> R.drawable.gas
            Incident.IncidentType.WATER_LEAK -> R.drawable.water_leaking
            Incident.IncidentType.OTHER -> R.drawable.warning
            null -> R.drawable.warning
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        // Animates a camera change focusing on the incident marker, and then shows the
        // incident info.
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(marker.position, 16F),
            object: GoogleMap.CancelableCallback {
            override fun onCancel() {

            }
            override fun onFinish() {
                val incident = marker.tag as Incident
                val alertDialog = AlertDialog.Builder(requireContext())
                    .setTitle(marker.title)
                    .setMessage(incident.description)
                    .setPositiveButton("Aceptar") { _, _ -> }
                alertDialog.show()
            }
        })

        // The alert dialog has shown, overriding the default marker behavior, thus returns true.
        return true
    }

}
