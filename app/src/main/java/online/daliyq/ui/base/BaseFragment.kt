package online.daliyq.ui.base

import androidx.fragment.app.Fragment
import online.daliyq.api.ApiService
import online.daliyq.db.AppDatabase

abstract class BaseFragment : Fragment() {
    val api: ApiService by lazy { ApiService.getInstance() }
    val db: AppDatabase by lazy { AppDatabase.getInstance(requireContext()) }
}