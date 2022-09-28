package mx.itesm.incidentesatizapan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import mx.itesm.incidentesatizapan.databinding.FragmentMainBinding
import mx.itesm.incidentesatizapan.databinding.FragmentManualBinding


class ManualFragment : Fragment() {

    private lateinit var binding: FragmentManualBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentManualBinding.inflate(layoutInflater)
        return binding.root


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registerEvents()
    }

    private fun registerEvents() {
        binding.btnIncendio.setOnClickListener {
            findNavController().navigate(
                ManualFragmentDirections.actionManualFragmentToIncendioFragment()
            )
        }
        binding.btnInundacion.setOnClickListener {
            findNavController().navigate(
                ManualFragmentDirections.actionManualFragmentToInundacionFragment()
            )
        }

        binding.btnTerremoto.setOnClickListener {
            findNavController().navigate(
                ManualFragmentDirections.actionManualFragmentToTerremotoFragment()
            )
        }


    }
}